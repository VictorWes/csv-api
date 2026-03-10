package com.csv.controller.request;

import jakarta.validation.constraints.NotBlank;

public record EmpresaRequest(
        @NotBlank(message = "O nome da empresa é obrigatório")
        String nome,

        String urlLogo
) {
}
