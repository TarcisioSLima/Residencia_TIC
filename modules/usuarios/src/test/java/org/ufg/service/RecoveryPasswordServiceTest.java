package org.ufg.service;

import io.quarkus.mailer.Mailer;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.ufg.dto.ResetPasswordResponseDTO;
import org.ufg.entity.Usuario;
import org.ufg.exception.ValidationException;
import org.ufg.repository.UsuarioRepository;
import org.ufg.util.SecurityUtils;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecoveryPasswordServiceTest {

    private static final String EMAIL = "user@ufg.br";
    private static final String IP = "10.0.0.1";
    private static final String UA = "JUnit";

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    SecurityUtils securityUtils;

    @Mock
    ValidationService validationService;

    @Mock
    Mailer mailer;

    @Mock
    RedisDataSource redisDataSource;

    @Mock
    @SuppressWarnings("unchecked")
    ValueCommands<String, String> valueCommands;

    @InjectMocks
    RecoveryPasswordService recoveryPasswordService;

    @BeforeEach
    void stubRedisValueCommands() {
        when(redisDataSource.value(String.class)).thenReturn(valueCommands);
    }

    @Test
    @DisplayName("verifyCode falha quando não há código no Redis")
    void verifyCode_missingStored_throws401() {
        when(valueCommands.get("pwd_recovery:" + EMAIL)).thenReturn(null);

        assertThrows(NotAuthorizedException.class,
                () -> recoveryPasswordService.verifyCode(EMAIL, "123456", IP, UA));
    }

    @Test
    @DisplayName("verifyCode apaga chave e falha após máximo de tentativas")
    void verifyCode_maxAttempts_throws401() {
        when(valueCommands.get("pwd_recovery:" + EMAIL)).thenReturn("123456:5");

        assertThrows(NotAuthorizedException.class,
                () -> recoveryPasswordService.verifyCode(EMAIL, "123456", IP, UA));

        verify(valueCommands).getdel("pwd_recovery:" + EMAIL);
    }

    @Test
    @DisplayName("verifyCode incrementa tentativas quando código está errado")
    void verifyCode_wrongCode_incrementsAttempts() {
        when(valueCommands.get("pwd_recovery:" + EMAIL)).thenReturn("111111:2");

        assertThrows(NotAuthorizedException.class,
                () -> recoveryPasswordService.verifyCode(EMAIL, "999999", IP, UA));

        verify(valueCommands).setex(eq("pwd_recovery:" + EMAIL), eq(600L), eq("111111:3"));
    }

    @Test
    @DisplayName("verifyCode em sucesso invalida código, grava token e retorna UUID")
    void verifyCode_success_returnsResetToken() {
        when(valueCommands.get("pwd_recovery:" + EMAIL)).thenReturn("654321:0");

        ResetPasswordResponseDTO dto = recoveryPasswordService.verifyCode(EMAIL, "654321", IP, UA);

        assertNotNull(dto.getResetToken());
        assertDoesNotThrow(() -> UUID.fromString(dto.getResetToken()));
        verify(valueCommands).getdel("pwd_recovery:" + EMAIL);
        verify(valueCommands).setex(startsWith("pwd_reset_token:"), eq(300L), eq(EMAIL));
    }

    @Test
    @DisplayName("resetPassword rejeita token inexistente com 401")
    void resetPassword_invalidToken_throws401() {
        when(valueCommands.get("pwd_reset_token:bad-token")).thenReturn(null);

        assertThrows(NotAuthorizedException.class,
                () -> recoveryPasswordService.resetPassword("bad-token", "Newpass1!", IP, UA));

        verify(valueCommands, never()).getdel(anyString());
    }

    @Test
    @DisplayName("resetPassword rejeita reutilização da senha atual")
    void resetPassword_samePassword_throwsBadRequest() {
        String token = UUID.randomUUID().toString();
        when(valueCommands.get("pwd_reset_token:" + token)).thenReturn(EMAIL);
        Usuario usuario = new Usuario();
        usuario.email = EMAIL;
        usuario.senha = "hash-old";
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(usuario);
        when(securityUtils.verifyPassword("SamePass1!", "hash-old")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> recoveryPasswordService.resetPassword(token, "SamePass1!", IP, UA));

        verify(valueCommands).getdel("pwd_reset_token:" + token);
        verify(validationService).validatePasswordStrength("SamePass1!");
        verify(securityUtils, never()).hashPassword(anyString());
    }

    @Test
    @DisplayName("resetPassword aplica hash e atualiza usuário quando token válido")
    void resetPassword_success_updatesPassword() {
        String token = UUID.randomUUID().toString();
        when(valueCommands.get("pwd_reset_token:" + token)).thenReturn(EMAIL);
        Usuario usuario = new Usuario();
        usuario.email = EMAIL;
        usuario.senha = "hash-old";
        usuario.dataUltimaEdicao = LocalDate.of(2020, 1, 1);
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(usuario);
        when(securityUtils.verifyPassword("ValidN3w!", "hash-old")).thenReturn(false);
        when(securityUtils.hashPassword("ValidN3w!")).thenReturn("hash-new");

        recoveryPasswordService.resetPassword(token, "ValidN3w!", IP, UA);

        verify(valueCommands).getdel("pwd_reset_token:" + token);
        verify(validationService).validatePasswordStrength("ValidN3w!");
        verify(securityUtils).hashPassword("ValidN3w!");
        assertEquals("hash-new", usuario.senha);
        assertEquals(LocalDate.now(), usuario.dataUltimaEdicao);
    }

    @Test
    @DisplayName("resetPassword delega validação de força da senha")
    void resetPassword_weakPassword_validationException() {
        String token = UUID.randomUUID().toString();
        when(valueCommands.get("pwd_reset_token:" + token)).thenReturn(EMAIL);
        doThrow(new ValidationException("fraca")).when(validationService).validatePasswordStrength("short");

        assertThrows(ValidationException.class,
                () -> recoveryPasswordService.resetPassword(token, "short", IP, UA));

        verify(valueCommands).getdel("pwd_reset_token:" + token);
    }

}
