package com.csv.controller;

import com.csv.AbstractIntegrationTest;
import com.csv.controller.request.EmpresaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class EmpresaControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper =new ObjectMapper();

    @Test
    @DisplayName("Deve criar uma empresa e retornar 201 Created")
    @WithMockUser(authorities = "ADMIN")
    void deveCriarEmpresaComSucesso() throws Exception {
        // ARRANGE
        var request = new EmpresaRequest("Loja do Victor", "http://logo.com/img.png");
        String payloadJson = objectMapper.writeValueAsString(request);

        // ACT & ASSERT
        mockMvc.perform(post("/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Loja do Victor"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando tentar criar empresa com nome vazio")
    @WithMockUser(authorities = "ADMIN")
    void deveRetornar400QuandoNomeVazio() throws Exception {
        // ARRANGE -
        var requestInvalido = new EmpresaRequest("", "http://logo.com/img.png");
        String payloadJson = objectMapper.writeValueAsString(requestInvalido);

        // ACT & ASSERT
        mockMvc.perform(post("/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden quando usuário não for ADMIN")
    @WithMockUser(username = "vendedor@teste.com", authorities = {"USER"})
    void deveRetornar403QuandoNaoForAdmin() throws Exception {
        var requestValido = new EmpresaRequest("Loja do Vendedor", "http://logo.com/img.png");
        String payloadJson = objectMapper.writeValueAsString(requestValido);

        mockMvc.perform(post("/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isForbidden());
    }
}
