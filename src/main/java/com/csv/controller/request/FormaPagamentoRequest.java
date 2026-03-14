package com.csv.controller.request;

import com.csv.enums.TipoBasePagamentoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FormaPagamentoRequest(
        @NotBlank(message = "O nome da forma de pagamento é obrigatório")
        String nome,

        @NotNull(message = "O tipo base de pagamento é obrigatório")
        TipoBasePagamentoEnum tipoBase,

        @NotNull(message = "O ID da empresa é obrigatório")
        UUID empresaId
) {
}
