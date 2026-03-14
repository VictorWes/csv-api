package com.csv.service;

import com.csv.controller.request.VendaAtualizacaoRequest;
import com.csv.controller.request.VendaRequest;
import com.csv.controller.response.VendaResponse;
import com.csv.entities.*;
import com.csv.mapper.VendaMapper;
import com.csv.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @InjectMocks private VendaService vendaService;

    @Mock private VendaRepository vendaRepository;
    @Mock private EmpresaRepository empresaRepository;
    @Mock private FormaPagamentoRepository formaPagamentoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private VendedorRepository vendedorRepository;
    @Mock private VendaMapper vendaMapper;
    @Mock private LancamentoFinanceiroService lancamentoFinanceiroService;

    private UUID vendaId;
    private UUID empresaId;
    private UUID formaPagamentoId;
    private Empresa empresa;
    private FormaPagamento formaPagamento;
    private Venda vendaEntidade;
    private VendaResponse vendaResponse;

    @BeforeEach
    void setup() {
        vendaId = UUID.randomUUID();
        empresaId = UUID.randomUUID();
        formaPagamentoId = UUID.randomUUID();

        empresa = new Empresa();
        empresa.setId(empresaId);

        formaPagamento = new FormaPagamento();
        formaPagamento.setId(formaPagamentoId);

        vendaEntidade = new Venda();
        vendaEntidade.setId(vendaId);
        vendaEntidade.setEmpresa(empresa);
        vendaEntidade.setFormaPagamento(formaPagamento);
        vendaEntidade.setValorTotal(BigDecimal.ZERO);

        vendaResponse = new VendaResponse(vendaId, BigDecimal.ZERO, empresaId, formaPagamentoId, null, null, true);
    }

    @Test
    @DisplayName("Deve abrir uma nova venda com sucesso")
    void deveAbrirVenda() {
        VendaRequest request = new VendaRequest(empresaId, formaPagamentoId, null, null);

        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(formaPagamentoRepository.findById(formaPagamentoId)).thenReturn(Optional.of(formaPagamento));
        when(vendaMapper.toEntity(request, empresa, formaPagamento, null, null)).thenReturn(vendaEntidade);
        when(vendaRepository.save(vendaEntidade)).thenReturn(vendaEntidade);
        when(vendaMapper.toResponse(vendaEntidade)).thenReturn(vendaResponse);

        VendaResponse response = vendaService.abrirVenda(request);

        assertNotNull(response);
        verify(vendaRepository).save(vendaEntidade);
    }

    @Test
    @DisplayName("Deve atualizar a forma de pagamento da venda")
    void deveAtualizarVenda() {
        UUID novaFormaId = UUID.randomUUID();
        FormaPagamento novaForma = new FormaPagamento();
        novaForma.setId(novaFormaId);

        VendaAtualizacaoRequest request = new VendaAtualizacaoRequest(novaFormaId, null, null);

        when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(vendaEntidade));
        when(formaPagamentoRepository.findById(novaFormaId)).thenReturn(Optional.of(novaForma));
        when(vendaMapper.toResponse(vendaEntidade)).thenReturn(vendaResponse);

        vendaService.atualizarVenda(vendaId, request);

        assertEquals(novaForma, vendaEntidade.getFormaPagamento());
    }

    @Test
    @DisplayName("Deve inativar (cancelar) a venda")
    void deveCancelarVenda() {
        when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(vendaEntidade));
        vendaService.cancelarVenda(vendaId);
        assertFalse(vendaEntidade.getAtivo());
    }

    @Test
    @DisplayName("Deve finalizar venda, acionar financeiro e recalcular total")
    void deveFinalizarVendaComSucesso() {
        UUID contaId = UUID.randomUUID();

        ItemVenda item = new ItemVenda();
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("50.00"));
        item.setAtivo(true);
        vendaEntidade.setItens(List.of(item));

        when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(vendaEntidade));
        when(vendaRepository.save(vendaEntidade)).thenReturn(vendaEntidade);
        when(vendaMapper.toResponse(vendaEntidade)).thenReturn(new VendaResponse(vendaId, new BigDecimal("100.00"), empresaId, formaPagamentoId, null, null, true));

        VendaResponse response = vendaService.finalizarVenda(vendaId, contaId);

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), vendaEntidade.getValorTotal());
        verify(lancamentoFinanceiroService, times(1)).criar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar fechar venda com valor zero")
    void deveBloquearVendaZerada() {
        UUID contaId = UUID.randomUUID();
        vendaEntidade.setItens(List.of());
        when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(vendaEntidade));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            vendaService.finalizarVenda(vendaId, contaId);
        });

        assertEquals("Não é possível finalizar uma venda com valor zero. Adicione itens ao carrinho.", exception.getMessage());
        verify(lancamentoFinanceiroService, never()).criar(any());
    }
}