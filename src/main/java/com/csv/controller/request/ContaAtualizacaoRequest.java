package com.csv.controller.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ContaAtualizacaoRequest(
        @NotNull(message = "O saldo para ajuste é obrigatório")
        BigDecimal saldoAtual
) {
}
