package org.ufg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CanalRequestDTO {

    @NotBlank(message = "O nome do canal e obrigatorio.")
    @Size(max = 45, message = "O nome do canal deve ter no maximo 45 caracteres.")
    private String nomeCanal;

    public String getNomeCanal() {
        return nomeCanal;
    }

    public void setNomeCanal(String nomeCanal) {
        this.nomeCanal = nomeCanal;
    }
}
