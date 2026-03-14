package com.csv.service;

import com.csv.controller.request.LancamentoAtualizacaoRequest;
import com.csv.controller.request.LancamentoFinanceiroRequest;
import com.csv.controller.response.LancamentoFinanceiroResponse;
import com.csv.entities.Conta;
import com.csv.entities.LancamentoFinanceiro;
import com.csv.enums.TipoOperacaoEnum;
import com.csv.mapper.LancamentoFinanceiroMapper;
import com.csv.repository.ContaRepository;
import com.csv.repository.LancamentoFinanceiroRepository;
import com.csv.repository.VendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LancamentoFinanceiroServiceTest {

    @InjectMocks private LancamentoFinanceiroService lancamentoService;
    @Mock private LancamentoFinanceiroRepository lancamentoRepository;
    @Mock private ContaRepository contaRepository;
    @Mock private VendaRepository vendaRepository;
    @Mock private LancamentoFinanceiroMapper lancamentoMapper;

    private UUID lancamentoId;
    private UUID contaId;
    private Conta conta;
    private LancamentoFinanceiro lancamentoEntidade;

    @BeforeEach
    void setup() {
        lancamentoId = UUID.randomUUID();
        contaId = UUID.randomUUID();

        conta = new Conta();
        conta.setId(contaId);
        conta.setSaldoAtual(new BigDecimal("1000.00"));

        lancamentoEntidade = new LancamentoFinanceiro();
        lancamentoEntidade.setId(lancamentoId);
        lancamentoEntidade.setConta(conta);
    }

    @Test
    @DisplayName("Deve criar lançamento de ENTRADA e somar no saldo da Conta")
    void deveCriarLancamentoEntrada() {
        var request = new LancamentoFinanceiroRequest(contaId, TipoOperacaoEnum.ENTRADA, new BigDecimal("200.00"), "Receita", null);
        var responseEsperado = new LancamentoFinanceiroResponse(lancamentoId, contaId, TipoOperacaoEnum.ENTRADA, new BigDecimal("200.00"), "Receita", null);

        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));
        when(lancamentoMapper.toEntity(request, conta, null)).thenReturn(lancamentoEntidade);
        when(lancamentoRepository.save(lancamentoEntidade)).thenReturn(lancamentoEntidade);
        when(lancamentoMapper.toResponse(lancamentoEntidade)).thenReturn(responseEsperado);

        LancamentoFinanceiroResponse response = lancamentoService.criar(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("1200.00"), conta.getSaldoAtual()); // 1000 + 200
        verify(lancamentoRepository).save(lancamentoEntidade);
    }

    @Test
    @DisplayName("Deve criar lançamento de SAIDA e subtrair do saldo da Conta")
    void deveCriarLancamentoSaida() {
        var request = new LancamentoFinanceiroRequest(contaId, TipoOperacaoEnum.SAIDA, new BigDecimal("300.00"), "Despesa", null);

        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));
        when(lancamentoMapper.toEntity(request, conta, null)).thenReturn(lancamentoEntidade);
        when(lancamentoRepository.save(lancamentoEntidade)).thenReturn(lancamentoEntidade);

        lancamentoService.criar(request);

        assertEquals(new BigDecimal("700.00"), conta.getSaldoAtual()); // 1000 - 300
    }

    @Test
    @DisplayName("Deve inativar uma ENTRADA e fazer o estorno contábil (subtrair)")
    void deveEstornarEntradaAoInativar() {
        lancamentoEntidade.setTipoOperacao(TipoOperacaoEnum.ENTRADA);
        lancamentoEntidade.setValor(new BigDecimal("150.00"));

        when(lancamentoRepository.findById(lancamentoId)).thenReturn(Optional.of(lancamentoEntidade));

        lancamentoService.inativar(lancamentoId);

        assertEquals(new BigDecimal("850.00"), conta.getSaldoAtual());
        assertFalse(lancamentoEntidade.getAtivo());
    }
}