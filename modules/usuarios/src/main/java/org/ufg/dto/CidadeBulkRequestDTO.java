package org.ufg.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CidadeBulkRequestDTO {

    @NotEmpty(message = "A lista de cidades nao pode estar vazia.")
    @Valid
    private List<CidadeRequestDTO> cidades;

    public List<CidadeRequestDTO> getCidades() {
        return cidades;
    }

    public void setCidades(List<CidadeRequestDTO> cidades) {
        this.cidades = cidades;
    }
}
