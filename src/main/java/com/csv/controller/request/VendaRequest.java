package com.csv.controller.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record VendaRequest(
        @NotNull(message = "A empresa é obrigatória para abrir a venda")
        UUID empresaId,

        @NotNull(message = "A forma de pagamento inicial é obrigatória")
        UUID formaPagamentoId,

        UUID clienteId,
        UUID vendedorId
) {
}