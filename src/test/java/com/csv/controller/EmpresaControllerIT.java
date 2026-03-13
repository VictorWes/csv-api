package com.csv.controller;

import com.csv.AbstractIntegrationTest;
import com.csv.controller.request.EmpresaAtualizacaoRequest;
import com.csv.controller.request.EmpresaRequest;
import com.csv.entities.Empresa;
import com.csv.repository.ClienteRepository;
import com.csv.repository.EmpresaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class EmpresaControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private ObjectMapper objectMapper =new ObjectMapper();

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        clienteRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    private Empresa criarEmpresaNoBanco(boolean ativo, String nome) {
        Empresa empresa = new Empresa();
        empresa.setNome(nome);
        empresa.setUrlLogo("http://logo.com/img.png");
        if (!ativo) {
            empresa.inativar();
        }
        return empresaRepository.save(empresa);
    }

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

    @Test
    @DisplayName("Deve listar apenas empresas ativas (200 OK)")
    @WithMockUser(authorities = "OPERADOR")
    void deveListarEmpresasAtivas() throws Exception {
        criarEmpresaNoBanco(true, "Empresa Ativa");
        criarEmpresaNoBanco(false, "Empresa Inativa");

        mockMvc.perform(get("/empresas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Empresa Ativa"));
    }

    @Test
    @DisplayName("Deve listar empresas inativas quando usuário for ADMIN (200 OK)")
    @WithMockUser(authorities = "ADMIN")
    void deveListarEmpresasInativas() throws Exception {
        criarEmpresaNoBanco(true, "Empresa Ativa");
        criarEmpresaNoBanco(false, "Empresa Inativa");

        mockMvc.perform(get("/empresas/inativas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Empresa Inativa"));
    }


    @Test
    @DisplayName("Deve atualizar o nome da empresa (200 OK)")
    @WithMockUser(authorities = "GERENTE") // Gerente também pode alterar
    void deveAtualizarEmpresa() throws Exception {
        Empresa empresa = criarEmpresaNoBanco(true, "Empresa Antiga");
        EmpresaAtualizacaoRequest request = new EmpresaAtualizacaoRequest("Empresa Nova", null);

        mockMvc.perform(patch("/empresas/{id}", empresa.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Empresa Nova"));
    }


    @Test
    @DisplayName("Deve inativar empresa com sucesso no banco (204 No Content)")
    @WithMockUser(authorities = "ADMIN")
    void deveInativarEmpresa() throws Exception {
        Empresa empresa = criarEmpresaNoBanco(true, "Empresa Para Deletar");

        mockMvc.perform(delete("/empresas/{id}", empresa.getId()))
                .andExpect(status().isNoContent());

        Empresa empresaNoBanco = empresaRepository.findById(empresa.getId()).orElseThrow();
        assertFalse(empresaNoBanco.getAtivo());
    }
}
