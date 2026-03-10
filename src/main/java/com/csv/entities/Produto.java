package com.csv.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_produto")
@Data
public class Produto extends BaseEntity{

    @NotBlank(message = "O nome do produto é obrigatório")
    @Column(nullable = false, length = 150)
    private String nome;

    @NotNull(message = "O preço é obrigatório")
    @Column(nullable = false)
    private BigDecimal preco;

    @Column(length = 100)
    private String segmento;

    @Column(name = "url_foto")
    private String urlFoto;

    @Column(name = "codigo_barras", length = 100)
    private String codigoBarras;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}
