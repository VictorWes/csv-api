package com.csv.controller.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmpresaResponse(
        UUID id,
        String nome,
        String urlLogo,
        LocalDateTime dataCriacao
) {
}
