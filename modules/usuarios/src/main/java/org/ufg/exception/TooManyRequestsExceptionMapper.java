package org.ufg.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class TooManyRequestsExceptionMapper implements ExceptionMapper<TooManyRequestsException> {

    @Override
    public Response toResponse(TooManyRequestsException exception) {
        String msg = exception.getMessage() != null ? exception.getMessage() : "Too many requests";
        return Response.status(429)
                .entity(Map.of("error", msg))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
