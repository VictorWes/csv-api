package com.csv.controller.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProdutoAtualizacaoRequest (
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        @DecimalMin(value = "0.0", inclusive = true, message = "O preço não pode ser negativo")
        BigDecimal preco,

        @Size(max = 100)
        String segmento,

        String urlFoto,

        @Size(max = 100)
        String codigoBarras
) {
}
