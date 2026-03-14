package com.csv.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_item_venda")
@Data
public class ItemVenda extends BaseEntity {
    @NotNull
    @Column(nullable = false)
    private Integer quantidade;

    @NotNull
    @Column(name = "preco_unitario", nullable = false)
    private BigDecimal precoUnitario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Boolean ativo = true;

    public void inativar() {
        this.ativo = false;
    }

    public void atualizarQuantidade(Integer novaQuantidade) {
        if (novaQuantidade != null && novaQuantidade > 0) {
            this.quantidade = novaQuantidade;
        }
    }

    public BigDecimal getSubtotal() {
        if (this.precoUnitario == null || this.quantidade == null) {
            return BigDecimal.ZERO;
        }
        return this.precoUnitario.multiply(BigDecimal.valueOf(this.quantidade));
    }
}
