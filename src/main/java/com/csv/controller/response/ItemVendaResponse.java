package com.csv.controller.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemVendaResponse (
        UUID id,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal,
        UUID vendaId,
        UUID produtoId,
        String nomeProduto
){

}
