package org.ufg.dto;

import java.util.UUID;

public class PossivelStatusResponseDTO {
    private UUID id;
    private String nomeStatus;
    private UUID idCanal;

    public PossivelStatusResponseDTO() {
    }

    public PossivelStatusResponseDTO(UUID id, String nomeStatus, UUID idCanal) {
        this.id = id;
        this.nomeStatus = nomeStatus;
        this.idCanal = idCanal;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
