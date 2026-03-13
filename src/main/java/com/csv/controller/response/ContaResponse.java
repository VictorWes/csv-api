package com.csv.controller.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaResponse(
        UUID id,
        BigDecimal saldoAtual,
        UUID empresaId
) {
}
