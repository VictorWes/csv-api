package com.csv.controller;

import com.csv.AbstractIntegrationTest;
import com.csv.controller.request.LancamentoAtualizacaoRequest;
import com.csv.controller.request.LancamentoFinanceiroRequest;
import com.csv.entities.Conta;
import com.csv.entities.Empresa;
import com.csv.entities.LancamentoFinanceiro;
import com.csv.enums.TipoOperacaoEnum;
import com.csv.repository.ContaRepository;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.LancamentoFinanceiroRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class LancamentoFinanceiroControllerIT extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private LancamentoFinanceiroRepository lancamentoRepository;
    @Autowired private ContaRepository contaRepository;
    @Autowired private EmpresaRepository empresaRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Empresa empresa;
    private Conta conta;

    @BeforeEach
    void setUp() {
        lancamentoRepository.deleteAll();
        contaRepository.deleteAll();
        empresaRepository.deleteAll();

        empresa = new Empresa();
        empresa.setNome("Loja Financeira");
        empresa = empresaRepository.save(empresa);

        conta = new Conta();
        conta.setEmpresa(empresa);
        conta.setSaldoAtual(BigDecimal.ZERO);
        conta = contaRepository.save(conta);
    }

    @AfterEach
    void tearDown() {
        lancamentoRepository.deleteAll();
        contaRepository.deleteAll();
        empresaRepository.deleteAll();
    }


    @Test
    @DisplayName("Deve criar lançamento com sucesso (GERENTE)")
    @WithMockUser(authorities = "GERENTE")
    void deveCriarLancamento() throws Exception {
        var request = new LancamentoFinanceiroRequest(conta.getId(), TipoOperacaoEnum.ENTRADA, new BigDecimal("500.00"), "Aporte Inicial", null);

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valor").value(500.00));
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao tentar criar lançamento sendo OPERADOR")
    @WithMockUser(authorities = "OPERADOR")
    void deveBloquearCriacaoParaOperador() throws Exception {
        var request = new LancamentoFinanceiroRequest(conta.getId(), TipoOperacaoEnum.SAIDA, new BigDecimal("100.00"), "Despesa", null);

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve atualizar apenas a descrição do lançamento (ADMIN)")
    @WithMockUser(authorities = "ADMIN")
    void deveAtualizarDescricao() throws Exception {
        LancamentoFinanceiro lancamento = new LancamentoFinanceiro();
        lancamento.setConta(conta);
        lancamento.setTipoOperacao(TipoOperacaoEnum.ENTRADA);
        lancamento.setValor(new BigDecimal("100.00"));
        lancamento.setDescricao("Venda 01");
        lancamento = lancamentoRepository.save(lancamento);

        var request = new LancamentoAtualizacaoRequest("Venda 01 Corrigida");

        mockMvc.perform(patch("/lancamentos/{id}", lancamento.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Venda 01 Corrigida"));
    }


    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar lançar valor zero ou negativo")
    @WithMockUser(authorities = "GERENTE")
    void deveRetornar400ParaValorInvalido() throws Exception {
        var request = new LancamentoFinanceiroRequest(conta.getId(), TipoOperacaoEnum.ENTRADA, new BigDecimal("0.00"), "Erro", null);

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar lançar sem descrição")
    @WithMockUser(authorities = "GERENTE")
    void deveRetornar400ParaDescricaoVazia() throws Exception {
        var request = new LancamentoFinanceiroRequest(conta.getId(), TipoOperacaoEnum.SAIDA, new BigDecimal("50.00"), "", null);

        mockMvc.perform(post("/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}