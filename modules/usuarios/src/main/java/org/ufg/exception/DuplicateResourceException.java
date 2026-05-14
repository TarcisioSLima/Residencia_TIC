package org.ufg.exception;

/**
 * Exceção lançada quando tenta-se criar um recurso duplicado
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
