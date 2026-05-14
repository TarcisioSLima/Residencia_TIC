package org.ufg.exception;

/** Limite de requisições excedido (HTTP 429). Exceção de domínio; o mapper produz o corpo JSON. */
public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String message) {
        super(message);
    }
}
