package org.ufg.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.ufg.dto.RecoveryPasswordRequestDTO;
import org.ufg.dto.ResetPasswordRequestDTO;
import org.ufg.dto.ResetPasswordResponseDTO;
import org.ufg.dto.VerifyCodeRequestDTO;
import org.ufg.service.RateLimitService;
import org.ufg.service.RecoveryPasswordService;

import java.time.Duration;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class AuthResource {

    private static final int RL_RECOVERY_LIMIT = 3;
    private static final Duration RL_RECOVERY_WINDOW = Duration.ofMinutes(15);

    private static final int RL_VERIFY_LIMIT = 5;
    private static final Duration RL_VERIFY_WINDOW = Duration.ofMinutes(15);

    private static final int RL_RESET_LIMIT = 3;
    private static final Duration RL_RESET_WINDOW = Duration.ofMinutes(15);

    @Inject
    RecoveryPasswordService recoveryPasswordService;

    @Inject
    RateLimitService rateLimitService;

    @POST
    @Path("/recovery-password")
    public Response requestRecovery(RecoveryPasswordRequestDTO dto, @Context HttpHeaders headers) {
        String ip = extractIp(headers);
        String rlKey = "rl:recovery:" + normalize(dto.getEmail()) + ":" + ip;
        rateLimitService.checkAndIncrement(rlKey, RL_RECOVERY_LIMIT, RL_RECOVERY_WINDOW);

        recoveryPasswordService.requestRecovery(dto.getEmail());

        return Response.ok()
                .entity(Map.of("message", "Se o e-mail existir em nossa base, você receberá um código em breve."))
                .build();
    }

    @POST
    @Path("/recovery-password/verify-code")
    public Response verifyCode(VerifyCodeRequestDTO dto, @Context HttpHeaders headers) {
        String ip = extractIp(headers);
        String userAgent = extractUserAgent(headers);
        String rlKey = "rl:verify:" + normalize(dto.getEmail());
        rateLimitService.checkAndIncrement(rlKey, RL_VERIFY_LIMIT, RL_VERIFY_WINDOW);

        ResetPasswordResponseDTO response =
                recoveryPasswordService.verifyCode(dto.getEmail(), dto.getCode(), ip, userAgent);

        return Response.ok(response).build();
    }

    @POST
    @Path("/recovery-password/reset")
    public Response resetPassword(ResetPasswordRequestDTO dto, @Context HttpHeaders headers) {
        String ip = extractIp(headers);
        String userAgent = extractUserAgent(headers);
        String rlKey = "rl:reset:" + normalize(dto.getResetToken());
        rateLimitService.checkAndIncrement(rlKey, RL_RESET_LIMIT, RL_RESET_WINDOW);

        recoveryPasswordService.resetPassword(dto.getResetToken(), dto.getNewPassword(), ip, userAgent);

        return Response.noContent().build();
    }

    private String extractIp(HttpHeaders headers) {
        String forwarded = headers.getHeaderString("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return "unknown";
    }

    private String extractUserAgent(HttpHeaders headers) {
        String ua = headers.getHeaderString("User-Agent");
        return ua != null ? ua : "unknown";
    }

    private String normalize(String value) {
        return value == null ? "null" : value.toLowerCase().replaceAll("\\s", "");
    }
}
