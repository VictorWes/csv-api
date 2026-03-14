package com.csv.entities;

import com.csv.enums.TipoOperacaoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_lancamento_financeiro")
@Data
public class LancamentoFinanceiro extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacao", nullable = false)
    private TipoOperacaoEnum tipoOperacao;

    @NotNull(message = "O valor é obrigatório")
    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false, length = 200)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id")
    private Venda venda;

    @Column(nullable = false)
    private Boolean ativo = true;

    public void inativar() {
        this.ativo = false;
    }

    public void atualizarDescricao(String descricao) {
        if (descricao != null && !descricao.isBlank()) {
            this.descricao = descricao;
        }
    }
}
