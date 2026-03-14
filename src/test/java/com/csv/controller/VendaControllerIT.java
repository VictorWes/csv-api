package com.csv.controller;

import com.csv.AbstractIntegrationTest;
import com.csv.controller.request.VendaRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class VendaControllerIT extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private VendaRepository vendaRepository;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private FormaPagamentoRepository formaPagamentoRepository;
    @Autowired private ContaRepository contaRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private ItemVendaRepository itemVendaRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Empresa empresa;
    private FormaPagamento formaPagamento;
    private Conta conta;
    private Produto produto;

    @BeforeEach
    void setUp() {
        itemVendaRepository.deleteAll();
        vendaRepository.deleteAll();
        formaPagamentoRepository.deleteAll();
        produtoRepository.deleteAll();
        contaRepository.deleteAll();
        empresaRepository.deleteAll();

        empresa = new Empresa();
        empresa.setNome("Supermercado CSV");
        empresa = empresaRepository.save(empresa);

        conta = new Conta();
        conta.setEmpresa(empresa);
        conta.setSaldoAtual(BigDecimal.ZERO);
        conta = contaRepository.save(conta);

        formaPagamento = new FormaPagamento();
        formaPagamento.setNome("PIX");
        formaPagamento.setEmpresa(empresa);
        formaPagamento.setTipoBase(com.csv.enums.TipoBasePagamentoEnum.DINHEIRO);
        formaPagamento = formaPagamentoRepository.save(formaPagamento);

        produto = new Produto();
        produto.setNome("Teclado");
        produto.setPreco(new BigDecimal("100.00"));
        produto.setEmpresa(empresa);
        produto = produtoRepository.save(produto);
    }

    @AfterEach
    void tearDown() {
        itemVendaRepository.deleteAll();
        vendaRepository.deleteAll();
        formaPagamentoRepository.deleteAll();
        produtoRepository.deleteAll();
        contaRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve abrir venda sendo OPERADOR (Retorna 201)")
    @WithMockUser(authorities = "OPERADOR")
    void deveAbrirVendaComoOperador() throws Exception {
        VendaRequest request = new VendaRequest(empresa.getId(), formaPagamento.getId(), null, null);

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valorTotal").value(0.0));
    }

    @Test
    @DisplayName("Deve FINALIZAR a venda via API e injetar saldo na Conta da Empresa")
    @WithMockUser(authorities = "OPERADOR")
    void deveFinalizarVendaEAtualizarSaldo() throws Exception {

        Venda venda = new Venda();
        venda.setEmpresa(empresa);
        venda.setFormaPagamento(formaPagamento);
        venda = vendaRepository.save(venda);


        ItemVenda item = new ItemVenda();
        item.setQuantidade(3);
        item.setPrecoUnitario(new BigDecimal("100.00"));
        item.setProduto(produto);
        item.setVenda(venda);
        itemVendaRepository.save(item);


        mockMvc.perform(post("/vendas/{id}/fechar", venda.getId())
                        .param("contaId", conta.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorTotal").value(300.00));

        Conta contaAtualizada = contaRepository.findById(conta.getId()).orElseThrow();
        assertEquals(new BigDecimal("300.00").compareTo(contaAtualizada.getSaldoAtual()), 0);
    }

    @Test
    @DisplayName("Deve CANCELAR a venda sendo GERENTE (Retorna 204)")
    @WithMockUser(authorities = "GERENTE")
    void deveCancelarVendaComoGerente() throws Exception {
        Venda venda = new Venda();
        venda.setEmpresa(empresa);
        venda.setFormaPagamento(formaPagamento);
        venda = vendaRepository.save(venda);

        mockMvc.perform(delete("/vendas/{id}", venda.getId()))
                .andExpect(status().isNoContent());

        Venda vendaInativa = vendaRepository.findById(venda.getId()).orElseThrow();
        assertFalse(vendaInativa.getAtivo());
    }

    @Test
    @DisplayName("Deve BLOQUEAR cancelamento de venda se for OPERADOR (Retorna 403)")
    @WithMockUser(authorities = "OPERADOR")
    void deveBloquearCancelamentoParaOperador() throws Exception {
        Venda venda = new Venda();
        venda.setEmpresa(empresa);
        venda.setFormaPagamento(formaPagamento);
        venda = vendaRepository.save(venda);

        mockMvc.perform(delete("/vendas/{id}", venda.getId()))
                .andExpect(status().isForbidden());
    }
}