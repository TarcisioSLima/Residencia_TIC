package org.ufg.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventoResponseDTO {
    private UUID id;
    private String nomeEvento;
    private Boolean personalizavel;
    private LocalDateTime horario;

    public EventoResponseDTO() {
    }

    public EventoResponseDTO(UUID id, String nomeEvento, Boolean personalizavel, LocalDateTime horario) {
        this.id = id;
        this.nomeEvento = nomeEvento;
        this.personalizavel = personalizavel;
        this.horario = horario;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }

    public Boolean getPersonalizavel() {
        return personalizavel;
    }

    public void setPersonalizavel(Boolean personalizavel) {
        this.personalizavel = personalizavel;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }
}
