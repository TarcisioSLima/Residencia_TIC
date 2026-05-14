package org.ufg.dto;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public class PerfilUsuarioDTO {
    @Valid
    private List<PreferenciaRequestDTO> preferencias;
    private List<UUID> idsCanaisPreferidos;

    public List<PreferenciaRequestDTO> getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(List<PreferenciaRequestDTO> preferencias) {
        this.preferencias = preferencias;
    }

    public List<UUID> getIdsCanaisPreferidos() {
        return idsCanaisPreferidos;
    }

    public void setIdsCanaisPreferidos(List<UUID> idsCanaisPreferidos) {
        this.idsCanaisPreferidos = idsCanaisPreferidos;
    }
}
