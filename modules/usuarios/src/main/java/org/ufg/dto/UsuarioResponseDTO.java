package org.ufg.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UsuarioResponseDTO {

    private UUID id;
    private String nome;
    private String email;
    private String whatsapp;
    private LocalDateTime dataCriacao;
    private LocalDate dataUltimaEdicao;
    private String nivelAcesso;
    private List<CanalResponseDTO> canaisPreferidos;

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(UUID id, String nome, String email, String whatsapp, LocalDateTime dataCriacao,
                              LocalDate dataUltimaEdicao, String nivelAcesso, List<CanalResponseDTO> canaisPreferidos) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.whatsapp = whatsapp;
        this.dataCriacao = dataCriacao;
        this.dataUltimaEdicao = dataUltimaEdicao;
        this.nivelAcesso = nivelAcesso;
        this.canaisPreferidos = canaisPreferidos;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataUltimaEdicao() {
        return dataUltimaEdicao;
    }

    public void setDataUltimaEdicao(LocalDate dataUltimaEdicao) {
        this.dataUltimaEdicao = dataUltimaEdicao;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public List<CanalResponseDTO> getCanaisPreferidos() {
        return canaisPreferidos;
    }

    public void setCanaisPreferidos(List<CanalResponseDTO> canaisPreferidos) {
        this.canaisPreferidos = canaisPreferidos;
    }
}
