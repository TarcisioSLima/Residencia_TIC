package org.ufg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class EventoRequestDTO {

    @NotBlank(message = "O nome do evento e obrigatorio.")
    @Size(max = 50, message = "O nome do evento deve ter no maximo 50 caracteres.")
    private String nomeEvento;

    @NotNull(message = "O campo personalizavel e obrigatorio.")
    private Boolean personalizavel;

    @NotNull(message = "O horario do evento e obrigatorio.")
    private LocalDateTime horario;

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
