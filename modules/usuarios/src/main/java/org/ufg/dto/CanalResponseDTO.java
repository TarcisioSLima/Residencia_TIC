package org.ufg.dto;

import java.time.LocalDate;
import java.util.UUID;

public class CanalResponseDTO {
    private UUID id;
    private String nomeCanal;
    private LocalDate dataInclusao;

    public CanalResponseDTO() {
    }

    public CanalResponseDTO(UUID id, String nomeCanal, LocalDate dataInclusao) {
        this.id = id;
        this.nomeCanal = nomeCanal;
        this.dataInclusao = dataInclusao;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeCanal() {
        return nomeCanal;
    }

    public void setNomeCanal(String nomeCanal) {
        this.nomeCanal = nomeCanal;
    }

    public LocalDate getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(LocalDate dataInclusao) {
        this.dataInclusao = dataInclusao;
    }
}
