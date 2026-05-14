package org.ufg.dto;

import java.util.UUID;

public class EnvioResponseDTO {
    private UUID id;
    private UUID idCanal;
    private UUID idAviso;
    private UUID idUsuarioDestinatario;
    private UUID idStatus;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdCanal() {
        return idCanal;
    }

    public void setIdCanal(UUID idCanal) {
        this.idCanal = idCanal;
    }

    public UUID getIdAviso() {
        return idAviso;
    }

    public void setIdAviso(UUID idAviso) {
        this.idAviso = idAviso;
    }

    public UUID getIdUsuarioDestinatario() {
        return idUsuarioDestinatario;
    }

    public void setIdUsuarioDestinatario(UUID idUsuarioDestinatario) {
        this.idUsuarioDestinatario = idUsuarioDestinatario;
    }

    public UUID getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(UUID idStatus) {
        this.idStatus = idStatus;
    }
}
