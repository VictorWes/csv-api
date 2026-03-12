package com.csv.controller;

import com.csv.AbstractIntegrationTest;
import com.csv.controller.request.AutenticacaoRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class AuthControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final String emailTeste = "admin@csv.com";
    private final String senhaTeste = "senhaSecreta123";

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        empresaRepository.deleteAll();

        Empresa empresa = new Empresa();
        empresa.setNome("Matriz CSV");
        empresa = empresaRepository.save(empresa);

        Usuario usuario = new Usuario();
        usuario.setNome("Administrador");
        usuario.setEmail(emailTeste);
        usuario.setSenha(passwordEncoder.encode(senhaTeste));
        usuario.setPerfil(PerfilEnum.ADMIN);
        usuario.setEmpresa(empresa);

        usuarioRepository.save(usuario);
    }

    @AfterEach
    void tearDown() {
        usuarioRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve autenticar com sucesso e retornar o Token JWT (200 OK)")
    void deveAutenticarComSucesso() throws Exception {
        // ARRANGE -
        var request = new AutenticacaoRequest(emailTeste, senhaTeste);
        String payloadJson = objectMapper.writeValueAsString(request);

        // ACT & ASSERT
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Deve retornar erro 403 Forbidden ao tentar logar com senha incorreta")
    void deveRetornarErroSenhaIncorreta() throws Exception {
        // ARRANGE -
        var request = new AutenticacaoRequest(emailTeste, "senhaErrada123");
        String payloadJson = objectMapper.writeValueAsString(request);

        // ACT & ASSERT
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar erro 403 Forbidden ao tentar logar com e-mail inexistente")
    void deveRetornarErroEmailInexistente() throws Exception {
        // ARRANGE -
        var request = new AutenticacaoRequest("fantasma@csv.com", senhaTeste);
        String payloadJson = objectMapper.writeValueAsString(request);

        // ACT & ASSERT
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isForbidden());
    }
}
