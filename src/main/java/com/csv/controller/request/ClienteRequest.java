package com.csv.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.UUID;

public record ClienteRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "O telefone é obrigatório")
        String telefone,

        @Past(message = "A data de nascimento deve ser no passado")
        LocalDate dataNascimento,

        @NotNull(message = "O ID da empresa é obrigatório")
        UUID empresaId
) {
}
