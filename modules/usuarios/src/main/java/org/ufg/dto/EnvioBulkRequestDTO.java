package org.ufg.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class EnvioBulkRequestDTO {

    @NotEmpty(message = "A lista de envios nao pode estar vazia.")
    @Valid
    private List<EnvioRequestDTO> envios;

    public List<EnvioRequestDTO> getEnvios() {
        return envios;
    }

    public void setEnvios(List<EnvioRequestDTO> envios) {
        this.envios = envios;
    }
}
