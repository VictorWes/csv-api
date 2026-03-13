package com.csv.controller.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoResponse (
        UUID id,
        String nome,
        BigDecimal preco,
        String segmento,
        String urlFoto,
        String codigoBarras,
        UUID empresaId
) {
}
