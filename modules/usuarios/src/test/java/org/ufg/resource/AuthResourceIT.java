package org.ufg.resource;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ufg.entity.Usuario;
import org.ufg.repository.UsuarioRepository;
import org.ufg.util.SecurityUtils;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@QuarkusTest
class AuthResourceIT {

    private static final String EMAIL = "auth-resource-it@ufg.br";
    private static final String XFF = "203.0.113.77";

    @InjectMock
    UsuarioRepository usuarioRepository;

    @Inject
    SecurityUtils securityUtils;

    @Inject
    RedisDataSource redisDataSource;

    @BeforeEach
    void cleanup() {
        String norm = EMAIL.toLowerCase().replaceAll("\\s", "");
        redisDataSource.value(String.class).getdel("pwd_recovery:" + EMAIL);
        redisDataSource.key().unlink("rl:recovery:" + norm + ":" + XFF);
        redisDataSource.key().unlink("rl:verify:" + norm);
    }

    @Test
    @DisplayName("OpenAPI expõe as rotas /auth/recovery-password (Swagger UI em /q/swagger-ui)")
    void openapi_includesAuthRecoveryPaths() {
        given()
                .when()
                .get("/q/openapi")
                .then()
                .statusCode(200)
                .body(containsString("/auth/recovery-password"))
                .body(containsString("/auth/recovery-password/verify-code"))
                .body(containsString("/auth/recovery-password/reset"));
    }

    @Test
    @DisplayName("POST /auth/recovery-password retorna 200 com mensagem genérica")
    void recoveryPassword_returns200Generic() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(null);

        given()
                .contentType(ContentType.JSON)
                .header("X-Forwarded-For", XFF)
                .body(Map.of("email", EMAIL))
                .when()
                .post("/auth/recovery-password")
                .then()
                .statusCode(200)
                .body("message", equalTo("Se o e-mail existir em nossa base, você receberá um código em breve."));
    }

    @Test
    @DisplayName("POST /auth/recovery-password retorna 429 após 3 requisições no mesmo e-mail/IP")
    void recoveryPassword_fourthRequest_returns429() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(null);

        for (int i = 0; i < 3; i++) {
            given()
                    .contentType(ContentType.JSON)
                    .header("X-Forwarded-For", XFF)
                    .body(Map.of("email", EMAIL))
                    .when()
                    .post("/auth/recovery-password")
                    .then()
                    .statusCode(200);
        }

        given()
                .contentType(ContentType.JSON)
                .header("X-Forwarded-For", XFF)
                .body(Map.of("email", EMAIL))
                .when()
                .post("/auth/recovery-password")
                .then()
                .statusCode(429)
                .body("error", notNullValue());
    }

    @Test
    @DisplayName("POST /auth/recovery-password/verify-code retorna 401 para código inválido")
    void verifyCode_wrongCode_returns401() {
        given()
                .contentType(ContentType.JSON)
                .header("X-Forwarded-For", XFF)
                .header("User-Agent", "AuthResourceIT")
                .body(Map.of("email", EMAIL, "code", "000000"))
                .when()
                .post("/auth/recovery-password/verify-code")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("POST /auth/recovery-password/reset retorna 401 para token inválido")
    void resetPassword_invalidToken_returns401() {
        given()
                .contentType(ContentType.JSON)
                .header("X-Forwarded-For", XFF)
                .header("User-Agent", "AuthResourceIT")
                .body(Map.of("resetToken", "not-a-real-token", "newPassword", "Newpass1!"))
                .when()
                .post("/auth/recovery-password/reset")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Fluxo completo: verify retorna resetToken e reset retorna 204")
    void fullFlow_verifyThenReset() {
        Usuario u = new Usuario();
        u.id = UUID.randomUUID();
        u.email = EMAIL;
        u.senha = securityUtils.hashPassword("Oldpass1!");
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(u);

        given()
                .contentType(ContentType.JSON)
                .header("X-Forwarded-For", XFF)
                .body(Map.of("email", EMAIL))
                .when()
                .post("/auth/recovery-password")
                .then()
                .statusCode(200);

        String stored = redisDataSource.value(String.class).get("pwd_recovery:" + EMAIL);
        assertTrue(stored != null && stored.matches("\\d{6}:\\d+"));
        String code = stored.split(":", 2)[0];

        String resetToken =
                given()
                        .contentType(ContentType.JSON)
                        .header("X-Forwarded-For", XFF)
                        .header("User-Agent", "AuthResourceIT")
                        .body(Map.of("email", EMAIL, "code", code))
                        .when()
                        .post("/auth/recovery-password/verify-code")
                        .then()
                        .statusCode(200)
                        .body("resetToken", notNullValue())
                        .extract()
                        .path("resetToken");

        given()
                .contentType(ContentType.JSON)
                .header("X-Forwarded-For", XFF)
                .header("User-Agent", "AuthResourceIT")
                .body(Map.of("resetToken", resetToken, "newPassword", "Newpass1!"))
                .when()
                .post("/auth/recovery-password/reset")
                .then()
                .statusCode(204);
    }
}
