package org.ufg.dto;

public class UsuarioRequestDTO {

    private String nome;
    private String email;
    private String whatsapp;
    private String senha;
    private String nivelAcesso;

    public UsuarioRequestDTO() {
    }

    public UsuarioRequestDTO(String nome, String email, String whatsapp, String senha, String nivelAcesso) {
        this.nome = nome;
        this.email = email;
        this.whatsapp = whatsapp;
        this.senha = senha;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }
}
