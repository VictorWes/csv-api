package com.csv.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_conta")
@Data
public class Conta extends BaseEntity{

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false, unique = true)
    private Empresa empresa;

    @Column(name = "saldo_atual", nullable = false)
    private BigDecimal saldoAtual = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean ativo = true;

    public void inativar() {
        this.ativo = false;
    }

    public void ajustarSaldo(BigDecimal novoSaldo) {
        if (novoSaldo != null) {
            this.saldoAtual = novoSaldo;
        }
    }

    public void creditar(BigDecimal valor) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
            this.saldoAtual = this.saldoAtual.add(valor);
        }
    }

    public void debitar(BigDecimal valor) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
            this.saldoAtual = this.saldoAtual.subtract(valor);
        }
    }
}
