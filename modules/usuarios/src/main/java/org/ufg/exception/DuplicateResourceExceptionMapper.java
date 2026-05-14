package org.ufg.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper global para tratamento de exceções de recurso duplicado
 */
@Provider
public class DuplicateResourceExceptionMapper implements ExceptionMapper<DuplicateResourceException> {

    private static final Logger LOG = Logger.getLogger(DuplicateResourceExceptionMapper.class);

    @Override
    public Response toResponse(DuplicateResourceException exception) {
        LOG.warnf("Recurso duplicado: %s", exception.getMessage());

        Map<String, Object> errorResponse = buildErrorResponse(
            Response.Status.CONFLICT.getStatusCode(),
            "Recurso Duplicado",
            exception.getMessage()
        );

        return Response
            .status(Response.Status.CONFLICT)
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
