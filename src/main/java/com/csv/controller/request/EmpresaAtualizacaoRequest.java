package com.csv.controller.request;

import jakarta.validation.constraints.Size;

public record EmpresaAtualizacaoRequest(
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        String urlLogo
) {
}
