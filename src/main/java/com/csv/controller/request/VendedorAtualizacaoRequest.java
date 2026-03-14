package com.csv.controller.request;

import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public record VendedorAtualizacaoRequest(
        String nome,
        String cargo,
        @Past(message = "A data de nascimento deve estar no passado")
        LocalDate dataNascimento
) {
}