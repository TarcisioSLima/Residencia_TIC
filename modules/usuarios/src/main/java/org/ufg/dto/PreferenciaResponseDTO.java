package org.ufg.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class PreferenciaResponseDTO {
    private UUID id;
    private UUID idUsuario;
    private UUID idEvento;
    private UUID idCidade;
    private LocalDate dataCriacao;
    private LocalDate dataUltimaEdicao;
    private BigDecimal valor;
    private Boolean personalizavel;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(UUID idUsuario) {
        this.idUsuario = idUsuario;
    }

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

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataUltimaEdicao() {
        return dataUltimaEdicao;
    }

    public void setDataUltimaEdicao(LocalDate dataUltimaEdicao) {
        this.dataUltimaEdicao = dataUltimaEdicao;
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
