package com.csv.controller.request;

import com.csv.enums.TipoOperacaoEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record LancamentoFinanceiroRequest(
        @NotNull(message = "O ID da conta é obrigatório")
        UUID contaId,

        @NotNull(message = "O tipo de operação é obrigatório")
        TipoOperacaoEnum tipoOperacao,

        @NotNull(message = "O valor é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor do lançamento deve ser maior que zero")
        BigDecimal valor,

        @NotBlank(message = "A descrição é obrigatória")
        String descricao,

        UUID vendaId
) {
}
