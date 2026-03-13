package com.csv.controller;

import com.csv.AbstractIntegrationTest;
import com.csv.controller.request.UsuarioAtualizacaoRequest;
import com.csv.controller.request.UsuarioRequest;
import com.csv.entities.Empresa;
import com.csv.entities.Usuario;
import com.csv.enums.PerfilEnum;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class UsuarioControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Empresa empresaSalva;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        empresaRepository.deleteAll();

        Empresa empresa = new Empresa();
        empresa.setNome("CSV Corporation");
        empresa.setUrlLogo("http://logo.com/csv.png");
        empresaSalva = empresaRepository.save(empresa);
    }

    @AfterEach
    void tearDown() {
        usuarioRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    // --- Helper Method para popular o Testcontainer ---
    private Usuario criarUsuarioNoBanco(boolean ativo, String email, PerfilEnum perfil) {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário Teste");
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode("senha123"));
        usuario.setEmpresa(empresaSalva);
        usuario.setPerfil(perfil);
        if (!ativo) {
            usuario.inativar();
        }
        return usuarioRepository.save(usuario);
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso e retornar 201 Created")
    @WithMockUser(authorities = "ADMIN")
    void deveCriarUsuarioComSucesso() throws Exception {
        var request = new UsuarioRequest("Victor Wesley", "victor@csv.com", "senhaForte123", empresaSalva.getId(), PerfilEnum.ADMIN);
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Victor Wesley"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando tentar criar usuário com empresa inexistente")
    @WithMockUser(authorities = "ADMIN")
    void deveRetornar404QuandoEmpresaNaoExistir() throws Exception {
        var request = new UsuarioRequest("Invasor", "invasor@csv.com", "senha", UUID.randomUUID(), PerfilEnum.OPERADOR);
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden quando usuário não for ADMIN no POST")
    @WithMockUser(authorities = "OPERADOR")
    void deveRetornar403QuandoNaoForAdmin() throws Exception {
        var request = new UsuarioRequest("Vendedor", "venda@csv.com", "senha", empresaSalva.getId(), PerfilEnum.OPERADOR);
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve listar apenas usuários ativos")
    @WithMockUser(authorities = "ADMIN")
    void deveListarUsuariosAtivos() throws Exception {
        criarUsuarioNoBanco(true, "ativo@csv.com", PerfilEnum.OPERADOR);
        criarUsuarioNoBanco(false, "inativo@csv.com", PerfilEnum.OPERADOR);

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].email").value("ativo@csv.com"));
    }


    @Test
    @DisplayName("Deve atualizar o perfil do usuário (200 OK) quando GERENTE")
    @WithMockUser(authorities = "GERENTE")
    void deveAtualizarUsuario() throws Exception {
        Usuario usuario = criarUsuarioNoBanco(true, "atualiza@csv.com", PerfilEnum.OPERADOR);
        UsuarioAtualizacaoRequest request = new UsuarioAtualizacaoRequest(null, null, PerfilEnum.GERENTE);

        mockMvc.perform(patch("/usuarios/{id}", usuario.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil").value("GERENTE"));
    }

    @Test
    @DisplayName("Deve inativar usuário com sucesso no banco (204 No Content)")
    @WithMockUser(authorities = "ADMIN")
    void deveInativarUsuario() throws Exception {
        Usuario usuario = criarUsuarioNoBanco(true, "deletar@csv.com", PerfilEnum.OPERADOR);

        mockMvc.perform(delete("/usuarios/{id}", usuario.getId()))
                .andExpect(status().isNoContent());

        Usuario usuarioNoBanco = usuarioRepository.findById(usuario.getId()).orElseThrow();
        assertFalse(usuarioNoBanco.isEnabled()); // Valida que o isEnabled reflete o inativar()
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao tentar inativar sendo OPERADOR")
    @WithMockUser(authorities = "OPERADOR")
    void deveBloquearDelecaoParaOperador() throws Exception {
        Usuario usuario = criarUsuarioNoBanco(true, "seguro@csv.com", PerfilEnum.OPERADOR);

        mockMvc.perform(delete("/usuarios/{id}", usuario.getId()))
                .andExpect(status().isForbidden());
    }
}
