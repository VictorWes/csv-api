package com.csv.controller.request;

import jakarta.validation.constraints.Size;

public record LancamentoAtualizacaoRequest (
        @Size(max = 200, message = "A descrição deve ter no máximo 200 caracteres")
        String descricao
) {
}
