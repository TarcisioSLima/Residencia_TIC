package org.ufg.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ufg.entity.Usuario;
import org.ufg.repository.UsuarioRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@QuarkusTest
class RecoveryPasswordServiceRequestRecoveryIT {

    private static final String EMAIL = "recovery-it@ufg.br";

    @Inject
    RecoveryPasswordService recoveryPasswordService;

    @Inject
    RedisDataSource redisDataSource;

    @Inject
    MockMailbox mockMailbox;

    @InjectMock
    UsuarioRepository usuarioRepository;

    @BeforeEach
    void cleanup() {
        mockMailbox.clear();
        redisDataSource.value(String.class).getdel("pwd_recovery:" + EMAIL);
    }

    @Test
    @DisplayName("requestRecovery grava Redis e envia e-mail HTML (mailer mock)")
    void requestRecovery_savesRedisAndSendsHtmlMail() {
        Usuario u = new Usuario();
        u.id = UUID.randomUUID();
        u.email = EMAIL;
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(u);

        recoveryPasswordService.requestRecovery(EMAIL);

        String stored = redisDataSource.value(String.class).get("pwd_recovery:" + EMAIL);
        assertNotNull(stored);
        assertTrue(stored.matches("\\d{6}:0"));

        List<Mail> mails = mockMailbox.getMailsSentTo(EMAIL);
        assertEquals(1, mails.size());
        String html = mails.get(0).getHtml();
        assertNotNull(html);
        assertTrue(html.contains("DOCTYPE html") || html.contains("Recuperação de senha"));
    }

    @Test
    @DisplayName("requestRecovery não grava Redis nem envia mail quando usuário não existe")
    void requestRecovery_unknownUser_silent() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(null);

        recoveryPasswordService.requestRecovery(EMAIL);

        assertNull(redisDataSource.value(String.class).get("pwd_recovery:" + EMAIL));
        assertTrue(mockMailbox.getMailsSentTo(EMAIL).isEmpty());
    }
}
