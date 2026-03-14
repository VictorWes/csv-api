package com.csv.service;

import com.csv.controller.request.FormaPagamentoAtualizacaoRequest;
import com.csv.controller.request.FormaPagamentoRequest;
import com.csv.controller.response.FormaPagamentoResponse;
import com.csv.entities.Empresa;
import com.csv.entities.FormaPagamento;
import com.csv.enums.TipoBasePagamentoEnum;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.FormaPagamentoMapper;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.FormaPagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormaPagamentoServiceTest {

    @InjectMocks
    private FormaPagamentoService formaPagamentoService;

    @Mock private FormaPagamentoRepository formaPagamentoRepository;
    @Mock private EmpresaRepository empresaRepository;
    @Mock private FormaPagamentoMapper formaPagamentoMapper;

    private UUID formaPagamentoId;
    private UUID empresaId;
    private FormaPagamentoRequest request;
    private Empresa empresa;
    private FormaPagamento formaPagamentoEntidade;
    private FormaPagamentoResponse responseEsperado;

    @BeforeEach
    void setup() {
        formaPagamentoId = UUID.randomUUID();
        empresaId = UUID.randomUUID();

        empresa = new Empresa();
        empresa.setId(empresaId);

        request = new FormaPagamentoRequest("Pix", TipoBasePagamentoEnum.PIX, empresaId);

        formaPagamentoEntidade = new FormaPagamento();
        formaPagamentoEntidade.setId(formaPagamentoId);
        formaPagamentoEntidade.setNome(request.nome());
        formaPagamentoEntidade.setTipoBase(request.tipoBase());
        formaPagamentoEntidade.setEmpresa(empresa);

        responseEsperado = new FormaPagamentoResponse(formaPagamentoId, "Pix", TipoBasePagamentoEnum.PIX, empresaId);
    }


    @Test
    @DisplayName("Deve criar uma forma de pagamento com sucesso")
    void deveCriarFormaPagamentoComSucesso() {
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(formaPagamentoMapper.toEntity(request, empresa)).thenReturn(formaPagamentoEntidade);
        when(formaPagamentoRepository.save(formaPagamentoEntidade)).thenReturn(formaPagamentoEntidade);
        when(formaPagamentoMapper.toResponse(formaPagamentoEntidade)).thenReturn(responseEsperado);

        FormaPagamentoResponse responseRecebido = formaPagamentoService.criarFormaPagamento(request);

        assertNotNull(responseRecebido);
        assertEquals(responseEsperado.id(), responseRecebido.id());
        verify(formaPagamentoRepository, times(1)).save(formaPagamentoEntidade);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar com empresa inexistente")
    void deveLancarErroQuandoEmpresaNaoExistir() {
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> formaPagamentoService.criarFormaPagamento(request));
        verify(formaPagamentoRepository, never()).save(any());
    }


    @Test
    @DisplayName("Deve buscar forma de pagamento por ID com sucesso")
    void deveBuscarPorId() {
        when(formaPagamentoRepository.findById(formaPagamentoId)).thenReturn(Optional.of(formaPagamentoEntidade));
        when(formaPagamentoMapper.toResponse(formaPagamentoEntidade)).thenReturn(responseEsperado);

        FormaPagamentoResponse response = formaPagamentoService.buscarPorId(formaPagamentoId);

        assertNotNull(response);
        assertEquals(formaPagamentoId, response.id());
    }

    @Test
    @DisplayName("Deve listar formas de pagamento ativas com paginação")
    void deveListarAtivas() {
        Pageable pageable = PageRequest.of(0, 10);
        when(formaPagamentoRepository.findAllByAtivoTrue(pageable)).thenReturn(new PageImpl<>(List.of(formaPagamentoEntidade)));
        when(formaPagamentoMapper.toResponse(formaPagamentoEntidade)).thenReturn(responseEsperado);

        Page<FormaPagamentoResponse> resultado = formaPagamentoService.listarTodas(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(formaPagamentoRepository, times(1)).findAllByAtivoTrue(pageable);
    }


    @Test
    @DisplayName("Deve atualizar o nome e tipo com sucesso (Domínio Rico)")
    void deveAtualizarFormaPagamento() {
        FormaPagamentoAtualizacaoRequest atualizacaoRequest = new FormaPagamentoAtualizacaoRequest("Cartão de Crédito Visa", TipoBasePagamentoEnum.PIX);

        when(formaPagamentoRepository.findById(formaPagamentoId)).thenReturn(Optional.of(formaPagamentoEntidade));
        when(formaPagamentoMapper.toResponse(formaPagamentoEntidade)).thenReturn(responseEsperado);

        FormaPagamentoResponse response = formaPagamentoService.atualizar(formaPagamentoId, atualizacaoRequest);

        assertNotNull(response);
        assertEquals("Cartão de Crédito Visa", formaPagamentoEntidade.getNome());
        assertEquals(TipoBasePagamentoEnum.PIX, formaPagamentoEntidade.getTipoBase());
    }

    @Test
    @DisplayName("Deve inativar a forma de pagamento (Soft Delete)")
    void deveInativarFormaPagamento() {
        when(formaPagamentoRepository.findById(formaPagamentoId)).thenReturn(Optional.of(formaPagamentoEntidade));

        formaPagamentoService.inativar(formaPagamentoId);
        verify(formaPagamentoRepository, times(1)).findById(formaPagamentoId);
    }

}