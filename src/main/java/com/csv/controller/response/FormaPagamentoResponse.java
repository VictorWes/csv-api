package com.csv.controller.response;

import com.csv.enums.TipoBasePagamentoEnum;

import java.util.UUID;

public record FormaPagamentoResponse(
        UUID id,
        String nome,
        TipoBasePagamentoEnum tipoBase,
        UUID empresaId
) {
}
