package com.csv.controller.request;

import jakarta.validation.constraints.Min;

public record ItemVendaAtualizacaoRequest (
        @Min(value = 1, message = "A quantidade mínima é 1")
        Integer quantidade
) {
}
