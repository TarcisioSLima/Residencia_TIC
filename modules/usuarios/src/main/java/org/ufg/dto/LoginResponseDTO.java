package org.ufg.dto;

import java.util.UUID;

public class LoginResponseDTO {
    private String token;
    private UUID id;
    private String nivelAcesso;
    private String nome;
    private String email;
    private String whatsapp;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, UUID id, String nivelAcesso) {
        this.token = token;
        this.id = id;
        this.nivelAcesso = nivelAcesso;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
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
}
