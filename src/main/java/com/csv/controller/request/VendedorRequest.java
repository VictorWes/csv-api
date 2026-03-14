package com.csv.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.UUID;

public record VendedorRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        String cargo,

        @Past(message = "A data de nascimento deve estar no passado")
        LocalDate dataNascimento,

        @NotNull(message = "O ID da empresa é obrigatório")
        UUID empresaId
) {
}