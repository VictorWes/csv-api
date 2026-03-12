package com.csv.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ClienteAtualizacaoRequest(

        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @Email(message = "Formato de e-mail inválido")
        String email,

        @Size(max = 11, message = "O telefone deve ter no máximo 11 caracteres")
        String telefone,

        @Past(message = "A data de nascimento deve ser no passado")
        LocalDate dataNascimento
) {
}
