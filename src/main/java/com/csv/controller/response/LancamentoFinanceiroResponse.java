package com.csv.controller.response;

import com.csv.enums.TipoOperacaoEnum;

import java.math.BigDecimal;
import java.util.UUID;

public record LancamentoFinanceiroResponse(
        UUID id,
        UUID contaId,
        TipoOperacaoEnum tipoOperacao,
        BigDecimal valor,
        String descricao,
        UUID vendaId
) {
}
