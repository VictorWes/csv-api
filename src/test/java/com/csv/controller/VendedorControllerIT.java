package com.csv.controller;

import com.csv.AbstractIntegrationTest;
import com.csv.controller.request.VendedorAtualizacaoRequest;
import com.csv.controller.request.VendedorRequest;
import com.csv.entities.Empresa;
import com.csv.entities.Vendedor;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.VendedorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class VendedorControllerIT extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private VendedorRepository vendedorRepository;
    @Autowired private EmpresaRepository empresaRepository;

    private ObjectMapper objectMapper;
    private Empresa empresa;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        vendedorRepository.deleteAll();
        empresaRepository.deleteAll();

        empresa = new Empresa();
        empresa.setNome("Loja de Roupas");
        empresa = empresaRepository.save(empresa);
    }

    @AfterEach
    void tearDown() {
        vendedorRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar vendedor com sucesso sendo GERENTE (Retorna 201)")
    @WithMockUser(authorities = "GERENTE")
    void deveCriarVendedorComoGerente() throws Exception {
        VendedorRequest request = new VendedorRequest("Ana Silva", "Caixa", LocalDate.of(1995, 3, 10), empresa.getId());

        mockMvc.perform(post("/vendedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Ana Silva"));
    }

    @Test
    @DisplayName("Deve BLOQUEAR criação de vendedor sendo OPERADOR (Retorna 403)")
    @WithMockUser(authorities = "OPERADOR")
    void deveBloquearCriacaoParaOperador() throws Exception {
        VendedorRequest request = new VendedorRequest("Ana Silva", "Caixa", LocalDate.of(1995, 3, 10), empresa.getId());

        mockMvc.perform(post("/vendedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve inativar vendedor com sucesso sendo ADMIN (Retorna 204)")
    @WithMockUser(authorities = "ADMIN")
    void deveInativarVendedor() throws Exception {
        Vendedor vendedor = new Vendedor();
        vendedor.setNome("Marcos");
        vendedor.setEmpresa(empresa);
        vendedor = vendedorRepository.save(vendedor);

        mockMvc.perform(delete("/vendedores/{id}", vendedor.getId()))
                .andExpect(status().isNoContent());

        Vendedor vendedorNoBanco = vendedorRepository.findById(vendedor.getId()).orElseThrow();
        assertFalse(vendedorNoBanco.getAtivo());
    }


    @Test
    @DisplayName("Deve retornar 400 Bad Request ao enviar Data de Nascimento no futuro")
    @WithMockUser(authorities = "GERENTE")
    void deveRetornar400ParaDataNoFuturo() throws Exception {
        // ARRANGE:
        LocalDate dataFutura = LocalDate.now().plusDays(1);
        VendedorRequest request = new VendedorRequest("Viajante do Tempo", "Vendedor", dataFutura, empresa.getId());

        // ACT & ASSERT
        mockMvc.perform(post("/vendedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao atualizar com Data de Nascimento no futuro")
    @WithMockUser(authorities = "GERENTE")
    void deveRetornar400ParaAtualizacaoComDataNoFuturo() throws Exception {
        Vendedor vendedor = new Vendedor();
        vendedor.setNome("João");
        vendedor.setEmpresa(empresa);
        vendedor = vendedorRepository.save(vendedor);

        LocalDate dataFutura = LocalDate.now().plusYears(5);
        VendedorAtualizacaoRequest request = new VendedorAtualizacaoRequest("João", null, dataFutura);

        mockMvc.perform(patch("/vendedores/{id}", vendedor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}