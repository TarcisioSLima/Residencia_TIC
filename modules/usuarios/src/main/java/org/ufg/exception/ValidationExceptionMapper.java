package org.ufg.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper global para tratamento de exceções de validação de negócio
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    private static final Logger LOG = Logger.getLogger(ValidationExceptionMapper.class);

    @Override
    public Response toResponse(ValidationException exception) {
        LOG.warnf("Erro de validação: %s", exception.getMessage());

        Map<String, Object> errorResponse = buildErrorResponse(
            Response.Status.BAD_REQUEST.getStatusCode(),
            "Erro de Validação",
            exception.getMessage()
        );

        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(errorResponse)
            .build();
    }

    private Map<String, Object> buildErrorResponse(int status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status);
        response.put("error", error);
        response.put("message", message);
        return response;
    }
}
