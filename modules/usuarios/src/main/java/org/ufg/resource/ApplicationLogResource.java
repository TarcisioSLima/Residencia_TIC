package org.ufg.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.ufg.dto.ApplicationLogResponseDTO;
import org.ufg.service.ApplicationLogService;

import java.util.List;

@Path("/list-logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
@Tag(name = "Logs de Aplicação", description = "Consulta de logs internos de execução das tasks do sistema")
public class ApplicationLogResource {

    @Inject
    ApplicationLogService applicationLogService;

    @GET
    @Operation(
        summary = "Lista logs de execução de uma task",
        description = "Retorna registros da tabela application_logs filtrados por task e data. " +
                      "Se date não for informada, usa a data atual. Protegido para ADMIN."
    )
    @Parameter(name = "task", description = "Identificador da task (ex: etl)", required = true, example = "etl")
    @Parameter(name = "date", description = "Data de referência no formato yyyy-MM-dd. Padrão: hoje.", required = false, example = "2026-03-09")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Lista de logs. Pode ser vazia ([]) quando não há registros para o dia."),
        @APIResponse(responseCode = "400", description = "Parâmetro task não informado ou data em formato inválido."),
        @APIResponse(responseCode = "401", description = "Token ausente ou inválido."),
        @APIResponse(responseCode = "403", description = "Usuário autenticado sem role ADMIN.")
    })
    public List<ApplicationLogResponseDTO> listLogs(
            @QueryParam("task") String task,
            @QueryParam("date") String date
    ) {
        return applicationLogService.listLogs(task, date);
    }
}
