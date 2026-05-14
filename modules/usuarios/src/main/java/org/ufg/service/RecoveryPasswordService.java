package org.ufg.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import org.jboss.logging.Logger;
import org.ufg.dto.ResetPasswordResponseDTO;
import org.ufg.entity.Usuario;
import org.ufg.repository.UsuarioRepository;
import org.ufg.templates.RecoveryPasswordEmail;
import org.ufg.util.SecurityUtils;

import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
public class RecoveryPasswordService {

    private static final Logger LOG = Logger.getLogger(RecoveryPasswordService.class);

    private static final String KEY_RECOVERY = "pwd_recovery:";
    private static final String KEY_RESET_TOKEN = "pwd_reset_token:";

    private static final Duration TTL_CODE = Duration.ofMinutes(10);
    private static final Duration TTL_TOKEN = Duration.ofMinutes(5);

    private static final int MAX_CODE_ATTEMPTS = 5;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    SecurityUtils securityUtils;

    @Inject
    ValidationService validationService;

    @Inject
    Mailer mailer;

    @Inject
    RedisDataSource redisDataSource;

    /**
     * Gera código de 6 dígitos, persiste no Redis e dispara e-mail.
     * Sempre retorna sem revelar se o e-mail existe (anti-enumeration).
     */
    public void requestRecovery(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario != null) {
            String code = generateCode();
            ValueCommands<String, String> commands = redisDataSource.value(String.class);
            commands.setex(KEY_RECOVERY + email, TTL_CODE.getSeconds(), code + ":0");
            sendRecoveryEmail(email, code);
            audit("recovery_requested", email, null, null);
        }
    }

    /**
     * Valida código. Após MAX_CODE_ATTEMPTS falhas, invalida o código.
     * Em sucesso, retorna um reset token single-use com TTL de 5min.
     */
    public ResetPasswordResponseDTO verifyCode(String email, String code, String ip, String userAgent) {
        ValueCommands<String, String> commands = redisDataSource.value(String.class);
        String stored = commands.get(KEY_RECOVERY + email);

        if (stored == null) {
            audit("code_failed", email, ip, userAgent);
            throw new NotAuthorizedException("Código inválido ou expirado.");
        }

        String[] parts = stored.split(":", 2);
        String storedCode = parts[0];
        int attempts = Integer.parseInt(parts[1]);

        if (attempts >= MAX_CODE_ATTEMPTS) {
            commands.getdel(KEY_RECOVERY + email);
            audit("code_failed", email, ip, userAgent);
            throw new NotAuthorizedException("Número máximo de tentativas atingido. Solicite um novo código.");
        }

        if (!storedCode.equals(code)) {
            commands.setex(KEY_RECOVERY + email, TTL_CODE.getSeconds(), storedCode + ":" + (attempts + 1));
            audit("code_failed", email, ip, userAgent);
            throw new NotAuthorizedException("Código inválido.");
        }

        commands.getdel(KEY_RECOVERY + email);

        String resetToken = UUID.randomUUID().toString();
        commands.setex(KEY_RESET_TOKEN + resetToken, TTL_TOKEN.getSeconds(), email);
        audit("code_verified", email, ip, userAgent);

        return new ResetPasswordResponseDTO(resetToken);
    }

    /**
     * Valida reset token (single-use), verifica força da senha, impede reutilização,
     * aplica BCrypt e invalida token.
     */
    @Transactional
    public void resetPassword(String resetToken, String newPassword, String ip, String userAgent) {
        ValueCommands<String, String> commands = redisDataSource.value(String.class);
        String email = commands.get(KEY_RESET_TOKEN + resetToken);

        if (email == null) {
            throw new NotAuthorizedException("Token inválido ou expirado.");
        }

        commands.getdel(KEY_RESET_TOKEN + resetToken);

        validationService.validatePasswordStrength(newPassword);

        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new NotAuthorizedException("Token inválido.");
        }

        if (securityUtils.verifyPassword(newPassword, usuario.senha)) {
            throw new BadRequestException("A nova senha não pode ser igual à senha atual.");
        }

        usuario.senha = securityUtils.hashPassword(newPassword);
        usuario.dataUltimaEdicao = LocalDate.now();

        audit("password_reset", email, ip, userAgent);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void sendRecoveryEmail(String to, String code) {
        String body = RecoveryPasswordEmail.Templates.recovery(code).render();
        mailer.send(
            Mail.withHtml(to, "Recuperação de senha — SIGEDAM", body)
        );
    }

    private void audit(String event, String email, String ip, String userAgent) {
        LOG.infof("[AUDIT] event=%s email=%s ip=%s userAgent=%s", event, email, ip, userAgent);
    }
}
