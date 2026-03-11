package com.csv.mapper;

import com.csv.controller.request.UsuarioRequest;
import com.csv.controller.response.UsuarioResponse;
import com.csv.entities.Empresa;
import com.csv.entities.Usuario;
import com.csv.enums.PerfilEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    private UsuarioMapper usuarioMapper;

    @BeforeEach
    void setUp() {
        usuarioMapper = new UsuarioMapper();
    }

    @Test
    @DisplayName("Deve converter UsuarioRequest e Empresa em uma entidade Usuario")
    void deveConverterRequestParaEntity() {
        // ARRANGE
        UUID empresaId = UUID.randomUUID();
        Empresa empresa = new Empresa();
        empresa.setId(empresaId);

        UsuarioRequest request = new UsuarioRequest(
                "Victor",
                "victor@gmail.com",
                "senha123",
                empresaId,
                PerfilEnum.ADMIN
        );

        // ACT
        Usuario entity = usuarioMapper.toEntity(request, empresa);

        // ASSERT
        assertNotNull(entity);
        assertEquals(request.nome(), entity.getNome());
        assertEquals(request.email(), entity.getEmail());
        assertEquals(request.perfil(), entity.getPerfil());
        assertEquals(empresa, entity.getEmpresa());
    }

    @Test
    @DisplayName("Deve converter entidade Usuario em UsuarioResponse")
    void deveConverterEntityParaResponse() {
        // ARRANGE
        UUID usuarioId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("Victor");
        usuario.setEmail("victor@gmail.com");
        usuario.setSenha("senha_criptografada_que_nao_deve_vazar");
        usuario.setPerfil(PerfilEnum.ADMIN);
        usuario.setEmpresa(empresa);

        // ACT
        UsuarioResponse response = usuarioMapper.toResponse(usuario);

        // ASSERT
        assertNotNull(response);
        assertEquals(usuario.getId(), response.id());
        assertEquals(usuario.getNome(), response.nome());
        assertEquals(usuario.getEmail(), response.email());
        assertEquals(usuario.getPerfil().name(), response.perfil());
        assertEquals(usuario.getEmpresa().getId(), response.empresaId());
    }
}