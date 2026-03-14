package com.csv.controller.request;

import java.util.UUID;

public record VendaAtualizacaoRequest(
        UUID formaPagamentoId,
        UUID clienteId,
        UUID vendedorId
) {
}