package com.csv.mapper;

import com.csv.controller.request.UsuarioRequest;
import com.csv.controller.response.UsuarioResponse;
import com.csv.entities.Empresa;
import com.csv.entities.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public Usuario toEntity(UsuarioRequest request, Empresa empresa) {
        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setPerfil(request.perfil());
        usuario.setEmpresa(empresa);

        return usuario;
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().name(),
                usuario.getEmpresa().getId()
        );
    }
}
