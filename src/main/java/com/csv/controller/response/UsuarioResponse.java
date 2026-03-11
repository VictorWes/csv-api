package com.csv.controller.response;

import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nome,
        String email,
        String perfil,
        UUID empresaId
) {
}
