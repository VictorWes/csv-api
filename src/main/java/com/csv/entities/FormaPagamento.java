package com.csv.entities;

import com.csv.enums.TipoBasePagamentoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "tb_forma_pagamento")
@Data
public class FormaPagamento extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_base", nullable = false, length = 20)
    private TipoBasePagamentoEnum tipoBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}
