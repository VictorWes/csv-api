package com.csv.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "tb_empresa")
@Data
public class Empresa extends BaseEntity{

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "url_logo")
    private String urlLogo;

    @Column(nullable = false)
    private Boolean ativo = true;

    public void inativar() {
        this.ativo = false;
    }

    public void atualizarInformacoes(String nome, String urlLogo) {
        if (nome != null && !nome.isBlank()) {
            this.nome = nome;
        }
        if (urlLogo != null && !urlLogo.isBlank()) {
            this.urlLogo = urlLogo;
        }
    }
}
