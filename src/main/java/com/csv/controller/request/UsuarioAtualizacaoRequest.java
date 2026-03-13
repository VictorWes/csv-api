package com.csv.controller.request;

import com.csv.enums.PerfilEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UsuarioAtualizacaoRequest(
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @Email(message = "Formato de e-mail inválido")
        String email,

        PerfilEnum perfil
) {
}
