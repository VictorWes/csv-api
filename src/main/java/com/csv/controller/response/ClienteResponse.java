package com.csv.controller.response;

import java.time.LocalDate;
import java.util.UUID;

public record ClienteResponse(
        UUID id,
        String nome,
        String email,
        String telefone,
        LocalDate dataNascimento, 
        UUID empresaId
) {
}
