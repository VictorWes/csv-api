package com.csv.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_conta")
@Data
public class Conta extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false, unique = true)
    private Empresa empresa;

    @Column(name = "saldo_atual", nullable = false)
    private BigDecimal saldoAtual = BigDecimal.ZERO;
}
