package org.ufg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class PossivelStatusRequestDTO {

    @NotBlank(message = "O nome do status e obrigatorio.")
    @Size(max = 45, message = "O nome do status deve ter no maximo 45 caracteres.")
    private String nomeStatus;

    @NotNull(message = "O ID do canal associado e obrigatorio.")
    private UUID idCanal;

    public String getNomeStatus() {
        return nomeStatus;
    }

    public void setNomeStatus(String nomeStatus) {
        this.nomeStatus = nomeStatus;
    }

    public UUID getIdCanal() {
        return idCanal;
    }

    public void setIdCanal(UUID idCanal) {
        this.idCanal = idCanal;
    }
}
