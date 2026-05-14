package org.ufg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CidadeRequestDTO {

    @NotBlank(message = "O nome da cidade e obrigatorio.")
    @Size(max = 100, message = "O nome da cidade deve ter no maximo 100 caracteres.")
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
