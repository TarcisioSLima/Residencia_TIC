package org.ufg.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    private static final Logger LOG = Logger.getLogger(ResourceNotFoundExceptionMapper.class);

    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        LOG.warnf("Resource not found: %s", exception.getMessage());
        return Response.status(Response.Status.NOT_FOUND)
                .entity(buildErrorResponse(
                        Response.Status.NOT_FOUND.getStatusCode(),
                        "Resource Not Found",
                        exception.getMessage()
                ))
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
