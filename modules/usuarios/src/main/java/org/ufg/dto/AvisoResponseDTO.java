package org.ufg.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class AvisoResponseDTO {

    private UUID id;
    private UUID idEvento;
    private String nomeEvento;
    private UUID idCidade;
    private String nomeCidade;
    private BigDecimal valor;
    private LocalDate dataGeracao;
    private LocalDate dataReferencia;
    private BigDecimal valorLimite;
    private String unidadeMedida;
    private BigDecimal diferenca;
    private LocalTime horario;
    private BigDecimal segundos;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(UUID idEvento) {
        this.idEvento = idEvento;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }

    public UUID getIdCidade() {
        return idCidade;
    }

    public void setIdCidade(UUID idCidade) {
        this.idCidade = idCidade;
    }

    public String getNomeCidade() {
        return nomeCidade;
    }

    public void setNomeCidade(String nomeCidade) {
        this.nomeCidade = nomeCidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDate dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public LocalDate getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(LocalDate dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public BigDecimal getValorLimite() {
        return valorLimite;
    }

    public void setValorLimite(BigDecimal valorLimite) {
        this.valorLimite = valorLimite;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public BigDecimal getSegundos() {
        return segundos;
    }

    public void setSegundos(BigDecimal segundos) {
        this.segundos = segundos;
    }
}
