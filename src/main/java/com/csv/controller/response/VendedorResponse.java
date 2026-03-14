package com.csv.controller.response;

import java.time.LocalDate;
import java.util.UUID;

public record VendedorResponse(
        UUID id,
        String nome,
        String cargo,
        LocalDate dataNascimento,
        UUID empresaId,
        Boolean ativo
) {
}