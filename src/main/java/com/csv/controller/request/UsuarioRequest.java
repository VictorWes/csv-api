package com.csv.controller.request;

import com.csv.enums.PerfilEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UsuarioRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @NotNull(message = "O ID da empresa é obrigatório")
        UUID empresaId,

        @NotNull(message = "O perfil do usuário é obrigatório")
        PerfilEnum perfil
) {
}
