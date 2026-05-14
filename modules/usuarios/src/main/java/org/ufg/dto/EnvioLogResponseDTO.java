package org.ufg.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class EnvioLogResponseDTO {
    private UUID id;
    private UUID idAviso;
    private UUID idCanal;
    private String nomeCanal;
    private UUID idStatus;
    private String nomeStatus;
    private UUID idUsuarioDestinatario;
    private String nomeUsuarioDestinatario;
    private String emailUsuarioDestinatario;
    private String whatsappUsuarioDestinatario;
    private UUID idEvento;
    private String nomeEvento;
    private UUID idCidade;
    private String nomeCidade;
    private LocalDate dataGeracaoAviso;
    private LocalDate dataReferenciaAviso;
    private OffsetDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdAviso() {
        return idAviso;
    }

    public void setIdAviso(UUID idAviso) {
        this.idAviso = idAviso;
    }

    public UUID getIdCanal() {
        return idCanal;
    }

    public void setIdCanal(UUID idCanal) {
        this.idCanal = idCanal;
    }

    public String getNomeCanal() {
        return nomeCanal;
    }

    public void setNomeCanal(String nomeCanal) {
        this.nomeCanal = nomeCanal;
    }

    public UUID getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(UUID idStatus) {
        this.idStatus = idStatus;
    }

    public String getNomeStatus() {
        return nomeStatus;
    }

    public void setNomeStatus(String nomeStatus) {
        this.nomeStatus = nomeStatus;
    }

    public UUID getIdUsuarioDestinatario() {
        return idUsuarioDestinatario;
    }

    public void setIdUsuarioDestinatario(UUID idUsuarioDestinatario) {
        this.idUsuarioDestinatario = idUsuarioDestinatario;
    }

    public String getNomeUsuarioDestinatario() {
        return nomeUsuarioDestinatario;
    }

    public void setNomeUsuarioDestinatario(String nomeUsuarioDestinatario) {
        this.nomeUsuarioDestinatario = nomeUsuarioDestinatario;
    }

    public String getEmailUsuarioDestinatario() {
        return emailUsuarioDestinatario;
    }

    public void setEmailUsuarioDestinatario(String emailUsuarioDestinatario) {
        this.emailUsuarioDestinatario = emailUsuarioDestinatario;
    }

    public String getWhatsappUsuarioDestinatario() {
        return whatsappUsuarioDestinatario;
    }

    public void setWhatsappUsuarioDestinatario(String whatsappUsuarioDestinatario) {
        this.whatsappUsuarioDestinatario = whatsappUsuarioDestinatario;
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

    public LocalDate getDataGeracaoAviso() {
        return dataGeracaoAviso;
    }

    public void setDataGeracaoAviso(LocalDate dataGeracaoAviso) {
        this.dataGeracaoAviso = dataGeracaoAviso;
    }

    public LocalDate getDataReferenciaAviso() {
        return dataReferenciaAviso;
    }

    public void setDataReferenciaAviso(LocalDate dataReferenciaAviso) {
        this.dataReferenciaAviso = dataReferenciaAviso;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
