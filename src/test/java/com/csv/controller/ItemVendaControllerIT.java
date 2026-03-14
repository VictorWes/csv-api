package com.csv.controller;

import com.csv.AbstractIntegrationTest;
import com.csv.controller.request.ItemVendaAtualizacaoRequest;
import com.csv.controller.request.ItemVendaRequest;
import com.csv.entities.*;
import com.csv.repository.*;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ItemVendaControllerIT extends AbstractIntegrationTest {

    @Autowired private FormaPagamentoRepository formaPagamentoRepository;
    @Autowired private MockMvc mockMvc;
    @Autowired private ItemVendaRepository itemVendaRepository;
    @Autowired private VendaRepository vendaRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private EmpresaRepository empresaRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Empresa empresa;
    private Produto produto;
    private Venda venda;

    @BeforeEach
    void setUp() {
        // Limpeza (A ordem importa: de quem depende para quem é dependido)
        itemVendaRepository.deleteAll();
        vendaRepository.deleteAll();
        produtoRepository.deleteAll();
        formaPagamentoRepository.deleteAll();
        empresaRepository.deleteAll();


        empresa = new Empresa();
        empresa.setNome("Loja IT");
        empresa = empresaRepository.save(empresa);


        produto = new Produto();
        produto.setNome("Monitor");
        produto.setPreco(new BigDecimal("800.00"));
        produto.setEmpresa(empresa);
        produto = produtoRepository.save(produto);


        FormaPagamento formaPagamento = new FormaPagamento();
        formaPagamento.setNome("Dinheiro Teste");
        formaPagamento.setTipoBase(com.csv.enums.TipoBasePagamentoEnum.DINHEIRO);
        formaPagamento.setEmpresa(empresa);
        formaPagamento = formaPagamentoRepository.save(formaPagamento);


        venda = new Venda();
        venda.setEmpresa(empresa);
        venda.setValorTotal(java.math.BigDecimal.ZERO);
        venda.setFormaPagamento(formaPagamento);
        venda = vendaRepository.save(venda);
    }

    @AfterEach
    void tearDown() {
        itemVendaRepository.deleteAll();
        vendaRepository.deleteAll();
        produtoRepository.deleteAll();
        formaPagamentoRepository.deleteAll();
        empresaRepository.deleteAll();
    }


    @Test
    @DisplayName("Deve adicionar item na venda e retornar 201 Created")
    @WithMockUser
    void deveAdicionarItem() throws Exception {
        var request = new ItemVendaRequest(2, new BigDecimal("800.00"), venda.getId(), produto.getId());

        mockMvc.perform(post("/itens-venda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.subtotal").value(1600.00));
    }

    @Test
    @DisplayName("Deve listar itens de uma venda específica")
    @WithMockUser
    void deveListarPorVenda() throws Exception {
        ItemVenda item = new ItemVenda();
        item.setQuantidade(1);
        item.setPrecoUnitario(new BigDecimal("10.00"));
        item.setVenda(venda);
        item.setProduto(produto);
        itemVendaRepository.save(item);

        mockMvc.perform(get("/itens-venda/venda/{vendaId}", venda.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("Deve atualizar a quantidade do item")
    @WithMockUser
    void deveAtualizarQuantidade() throws Exception {
        ItemVenda item = new ItemVenda();
        item.setQuantidade(1);
        item.setPrecoUnitario(new BigDecimal("50.00"));
        item.setVenda(venda);
        item.setProduto(produto);
        item = itemVendaRepository.save(item);

        var request = new ItemVendaAtualizacaoRequest(5);

        mockMvc.perform(patch("/itens-venda/{id}", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade").value(5))
                .andExpect(jsonPath("$.subtotal").value(250.00)); // Subtotal recalculado automaticamente!
    }

    @Test
    @DisplayName("Deve remover (inativar) o item da venda com sucesso")
    @WithMockUser
    void deveRemoverItem() throws Exception {
        ItemVenda item = new ItemVenda();
        item.setQuantidade(1);
        item.setPrecoUnitario(new BigDecimal("10.00"));
        item.setVenda(venda);
        item.setProduto(produto);
        item = itemVendaRepository.save(item);

        mockMvc.perform(delete("/itens-venda/{id}", item.getId()))
                .andExpect(status().isNoContent());

        ItemVenda itemNoBanco = itemVendaRepository.findById(item.getId()).orElseThrow();
        assertFalse(itemNoBanco.getAtivo());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar adicionar item com quantidade zero ou negativa")
    @WithMockUser
    void deveRetornar400QuandoQuantidadeInvalidaNoPost() throws Exception {
        var request = new ItemVendaRequest(0, new BigDecimal("100.00"), venda.getId(), produto.getId());

        // ACT & ASSERT
        mockMvc.perform(post("/itens-venda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar adicionar item com preço negativo")
    @WithMockUser
    void deveRetornar400QuandoPrecoNegativoNoPost() throws Exception {
        // ARRANGE
        var request = new ItemVendaRequest(1, new BigDecimal("-50.00"), venda.getId(), produto.getId());

        // ACT & ASSERT
        mockMvc.perform(post("/itens-venda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar adicionar item com Produto inexistente")
    @WithMockUser
    void deveRetornar404QuandoProdutoNaoExistir() throws Exception {
        // ARRANGE:
        var request = new ItemVendaRequest(1, new BigDecimal("100.00"), venda.getId(), UUID.randomUUID());

        // ACT & ASSERT
        mockMvc.perform(post("/itens-venda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar atualizar para quantidade inválida")
    @WithMockUser
    void deveRetornar400AoAtualizarQuantidadeInvalida() throws Exception {
        // ARRANGE:
        ItemVenda item = new ItemVenda();
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("50.00"));
        item.setVenda(venda);
        item.setProduto(produto);
        item = itemVendaRepository.save(item);

        var request = new ItemVendaAtualizacaoRequest(-1);

        // ACT & ASSERT
        mockMvc.perform(patch("/itens-venda/{id}", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}