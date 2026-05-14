package org.ufg.exception;

/**
 * Exceção lançada quando há violação de regras de negócio
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
