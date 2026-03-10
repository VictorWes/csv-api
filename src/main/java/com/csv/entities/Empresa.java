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
}
