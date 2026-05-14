package org.ufg.service;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ufg.exception.TooManyRequestsException;

import java.time.Duration;

@ApplicationScoped
public class RateLimitService {

    @Inject
    RedisDataSource redisDataSource;

    /**
     * Verifica e incrementa o contador de tentativas para uma chave (janela fixa).
     * Lança TooManyRequestsException se o limite for excedido.
     */
    public void checkAndIncrement(String key, int limit, Duration window) {
        ValueCommands<String, Long> commands = redisDataSource.value(Long.class);
        long newCount = commands.incr(key);
        if (newCount == 1L) {
            redisDataSource.key().expire(key, window);
        }
        if (newCount > limit) {
            throw new TooManyRequestsException(
                    "Muitas tentativas. Tente novamente em " + window.toMinutes() + " minutos.");
        }
    }
}
