package com.csv.controller.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoRequest(
        @NotBlank(message = "O nome do produto é obrigatório")
        String nome,

        @NotNull(message = "O preço é obrigatório")
        @DecimalMin(value = "0.0", inclusive = true, message = "O preço não pode ser negativo")
        BigDecimal preco,

        @NotNull(message = "O segmento é obrigatório")
        String segmento,
        String urlFoto,
        String codigoBarras,

        @NotNull(message = "O ID da empresa é obrigatório")
        UUID empresaId
) {


}
