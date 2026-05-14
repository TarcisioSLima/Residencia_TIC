package org.ufg.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class AvisoListDTO {

    @NotEmpty(message = "A lista de avisos nao pode ser vazia.")
    @Valid
    private List<AvisoDTO> avisos;

    public AvisoListDTO() {
    }

    public AvisoListDTO(List<AvisoDTO> avisos) {
        this.avisos = avisos;
    }

    public List<AvisoDTO> getAvisos() {
        return avisos;
    }

    public void setAvisos(List<AvisoDTO> avisos) {
        this.avisos = avisos;
    }
}
