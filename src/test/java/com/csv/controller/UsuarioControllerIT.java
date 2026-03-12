package com.csv.controller;

import com.csv.AbstractIntegrationTest;
import com.csv.controller.request.UsuarioRequest;
import com.csv.entities.Empresa;
import com.csv.enums.PerfilEnum;
import com.csv.repository.EmpresaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class UsuarioControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpresaRepository empresaRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Empresa empresaSalva;

    @BeforeEach
    void setUp() {
        Empresa empresa = new Empresa();
        empresa.setNome("CSV Corporation");
        empresa.setUrlLogo("http://logo.com/csv.png");
        empresaSalva = empresaRepository.save(empresa);
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso e retornar 201 Created")
    @WithMockUser(authorities = "ADMIN")
    void deveCriarUsuarioComSucesso() throws Exception {
        // ARRANGE
        var request = new UsuarioRequest(
                "Victor Wesley",
                "victor@csv.com",
                "senhaForte123",
                empresaSalva.getId(),
                PerfilEnum.ADMIN
        );
        String payloadJson = objectMapper.writeValueAsString(request);

        // ACT & ASSERT
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Victor Wesley"))
                .andExpect(jsonPath("$.empresaId").value(empresaSalva.getId().toString()));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando tentar criar usuário com empresa inexistente")
    @WithMockUser(authorities = "ADMIN")
    void deveRetornar404QuandoEmpresaNaoExistir() throws Exception {
        // ARRANGE
        var request = new UsuarioRequest(
                "Invasor",
                "invasor@csv.com",
                "senha123",
                UUID.randomUUID(),
                PerfilEnum.OPERADOR
        );
        String payloadJson = objectMapper.writeValueAsString(request);

        // ACT & ASSERT
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden quando usuário não for ADMIN")
    @WithMockUser(authorities = "OPERADOR")
    void deveRetornar403QuandoNaoForAdmin() throws Exception {
        // ARRANGE
        var request = new UsuarioRequest("Vendedor", "venda@csv.com", "senha", empresaSalva.getId(), PerfilEnum.OPERADOR);
        String payloadJson = objectMapper.writeValueAsString(request);

        // ACT & ASSERT
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isForbidden());
    }

}
