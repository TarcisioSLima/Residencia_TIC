package org.ufg.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class PreferenciaRequestDTO {

    @NotNull(message = "O ID do evento e obrigatorio.")
    private UUID idEvento;

    @NotNull(message = "O ID da cidade e obrigatorio.")
    private UUID idCidade;

    private BigDecimal valor;
    private Boolean personalizavel;

    public UUID getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(UUID idEvento) {
        this.idEvento = idEvento;
    }

    public UUID getIdCidade() {
        return idCidade;
    }

    public void setIdCidade(UUID idCidade) {
        this.idCidade = idCidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getPersonalizavel() {
        return personalizavel;
    }

    public void setPersonalizavel(Boolean personalizavel) {
        this.personalizavel = personalizavel;
    }
}
