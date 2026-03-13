package com.csv.controller;

import com.csv.AbstractIntegrationTest;
import com.csv.controller.request.ProdutoAtualizacaoRequest;
import com.csv.controller.request.ProdutoRequest;
import com.csv.entities.Empresa;
import com.csv.entities.Produto;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.ProdutoRepository;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ProdutoControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Empresa empresaSalva;

    @BeforeEach
    void setUp() {
        produtoRepository.deleteAll();
        empresaRepository.deleteAll();

        Empresa empresa = new Empresa();
        empresa.setNome("Loja de Eletrônicos CSV");
        empresaSalva = empresaRepository.save(empresa);
    }

    @AfterEach
    void tearDown() {
        produtoRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    private Produto criarProdutoNoBanco(boolean ativo, String nome, BigDecimal preco) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setPreco(preco);
        produto.setEmpresa(empresaSalva);
        if (!ativo) {
            produto.inativar();
        }
        return produtoRepository.save(produto);
    }


    @Test
    @DisplayName("Deve criar um produto com sucesso e retornar 201 Created (ADMIN)")
    @WithMockUser(authorities = "ADMIN")
    void deveCriarProdutoComSucesso() throws Exception {
        var request = new ProdutoRequest("Monitor 24", new BigDecimal("800.00"), "Monitores", null, "123", empresaSalva.getId());

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Monitor 24"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao enviar preço negativo")
    @WithMockUser(authorities = "ADMIN")
    void deveRetornar400QuandoPrecoNegativo() throws Exception {
        var request = new ProdutoRequest("Mouse", new BigDecimal("-50.00"), null, null, null, empresaSalva.getId());

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao tentar criar produto sendo OPERADOR")
    @WithMockUser(authorities = "OPERADOR")
    void deveBloquearCriacaoParaOperador() throws Exception {
        var request = new ProdutoRequest("Teclado", new BigDecimal("100.00"), null, null, null, empresaSalva.getId());

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("Deve listar produtos ativos com sucesso (OPERADOR tem acesso)")
    @WithMockUser(authorities = "OPERADOR")
    void deveListarProdutosAtivos() throws Exception {
        criarProdutoNoBanco(true, "Produto Ativo", new BigDecimal("10.00"));
        criarProdutoNoBanco(false, "Produto Inativo", new BigDecimal("5.00"));

        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Produto Ativo"));
    }


    @Test
    @DisplayName("Deve atualizar o produto com sucesso (200 OK) quando GERENTE")
    @WithMockUser(authorities = "GERENTE")
    void deveAtualizarProduto() throws Exception {
        Produto produto = criarProdutoNoBanco(true, "Mouse Velho", new BigDecimal("30.00"));
        ProdutoAtualizacaoRequest request = new ProdutoAtualizacaoRequest("Mouse Novo", new BigDecimal("50.00"), null, null, null);

        mockMvc.perform(patch("/produtos/{id}", produto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Mouse Novo"))
                .andExpect(jsonPath("$.preco").value(50.00));
    }


    @Test
    @DisplayName("Deve inativar produto com sucesso (204 No Content) quando ADMIN")
    @WithMockUser(authorities = "ADMIN")
    void deveInativarProduto() throws Exception {
        Produto produto = criarProdutoNoBanco(true, "Produto Para Deletar", new BigDecimal("100.00"));

        mockMvc.perform(delete("/produtos/{id}", produto.getId()))
                .andExpect(status().isNoContent());

        Produto produtoNoBanco = produtoRepository.findById(produto.getId()).orElseThrow();
        assertFalse(produtoNoBanco.getAtivo());
    }
}
