package com.csv.controller.request;

import com.csv.enums.TipoBasePagamentoEnum;
import jakarta.validation.constraints.Size;

public record FormaPagamentoAtualizacaoRequest(
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        TipoBasePagamentoEnum tipoBase
) {
}
