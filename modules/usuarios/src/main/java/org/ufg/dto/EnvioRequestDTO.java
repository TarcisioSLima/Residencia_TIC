package org.ufg.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class EnvioRequestDTO {

    @NotNull(message = "O ID do canal e obrigatorio.")
    private UUID idCanal;

    @NotNull(message = "O ID do aviso e obrigatorio.")
    private UUID idAviso;

    @NotNull(message = "O ID do usuario destinatario e obrigatorio.")
    private UUID idUsuarioDestinatario;

    @NotNull(message = "O ID do status e obrigatorio.")
    private UUID idStatus;

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
