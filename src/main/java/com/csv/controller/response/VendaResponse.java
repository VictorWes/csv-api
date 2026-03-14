package com.csv.controller.response;

import java.math.BigDecimal;
import java.util.UUID;

public record VendaResponse(
        UUID id,
        BigDecimal valorTotal,
        UUID empresaId,
        UUID formaPagamentoId,
        UUID clienteId,
        UUID vendedorId,
        Boolean ativo
) {
}