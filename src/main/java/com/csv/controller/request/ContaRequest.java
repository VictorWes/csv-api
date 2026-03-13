package com.csv.controller.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaRequest(
        @NotNull(message = "O ID da empresa é obrigatório")
        UUID empresaId,


        BigDecimal saldoInicial
) {

}
