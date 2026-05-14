package org.ufg.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.ufg.dto.EnvioBulkRequestDTO;
import org.ufg.dto.EnvioLogResponseDTO;
import org.ufg.dto.EnvioRequestDTO;
import org.ufg.dto.EnvioResponseDTO;
import org.ufg.dto.PageDTO;
import org.ufg.service.EnvioService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/envios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class EnvioResource {

    @Inject
    EnvioService envioService;

    @GET
    @Path("/metrics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMetrics() {
        Map<String, Long> metrics = envioService.getMetrics();
        return Response.ok(metrics).build();
    }

    @POST
    public Response create(@Valid EnvioRequestDTO dto) {
        try {
            EnvioResponseDTO newEnvio = envioService.createEnvio(dto);
            return Response.created(URI.create("/envios/" + newEnvio.getId()))
                    .entity(newEnvio)
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/bulk")
    public Response createBulk(@Valid EnvioBulkRequestDTO bulkDto) {
        try {
            List<EnvioResponseDTO> novosEnvios = envioService.createEnviosBulk(bulkDto);
            return Response.status(Response.Status.CREATED)
                    .entity(novosEnvios)
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    public List<EnvioResponseDTO> findAll() {
        return envioService.findAllEnvios();
    }

    @GET
    @Path("/{id: [0-9a-fA-F\\-]{36}}")
    public Response findById(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            EnvioResponseDTO envio = envioService.findEnvioById(uuid);
            return Response.ok(envio).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("ID inválido.").build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/logs")
    public Response buscarLogsComFiltros(
            @QueryParam("data") LocalDate data,
            @QueryParam("idEvento") String idEventoStr,
            @QueryParam("idCidade") String idCidadeStr,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        UUID idEvento = null;
        UUID idCidade = null;

        try {
            if (idEventoStr != null && !idEventoStr.isBlank()) {
                idEvento = UUID.fromString(idEventoStr);
            }
            if (idCidadeStr != null && !idCidadeStr.isBlank()) {
                idCidade = UUID.fromString(idCidadeStr);
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID de Evento ou Cidade inválido.")
                    .build();
        }

        PageDTO<EnvioLogResponseDTO> resultado = envioService.filtrarLogsEnvio(data, idEvento, idCidade, page, size);
        return Response.ok(resultado).build();
    }
}
