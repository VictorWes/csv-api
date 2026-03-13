package com.csv.service;

import com.csv.controller.request.ProdutoAtualizacaoRequest;
import com.csv.controller.request.ProdutoRequest;
import com.csv.controller.response.ProdutoResponse;
import com.csv.entities.Empresa;
import com.csv.entities.Produto;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.ProdutoMapper;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.ProdutoRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @InjectMocks
    private ProdutoService produtoService;

    @Mock private ProdutoRepository produtoRepository;
    @Mock private EmpresaRepository empresaRepository;
    @Mock private ProdutoMapper produtoMapper;

    private UUID produtoId;
    private UUID empresaId;
    private ProdutoRequest request;
    private Empresa empresa;
    private Produto produtoEntidade;
    private ProdutoResponse responseEsperado;

    @BeforeEach
    void setup() {
        produtoId = UUID.randomUUID();
        empresaId = UUID.randomUUID();

        empresa = new Empresa();
        empresa.setId(empresaId);

        request = new ProdutoRequest("Teclado Mecânico", new BigDecimal("350.00"), "Periféricos", null, "123456789", empresaId);

        produtoEntidade = new Produto();
        produtoEntidade.setId(produtoId);
        produtoEntidade.setNome(request.nome());
        produtoEntidade.setPreco(request.preco());
        produtoEntidade.setEmpresa(empresa);

        responseEsperado = new ProdutoResponse(produtoId, produtoEntidade.getNome(), produtoEntidade.getPreco(), "Periféricos", null, "123456789", empresaId);
    }


    @Test
    @DisplayName("Deve criar um produto com sucesso")
    void deveCriarProdutoComSucesso() {
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(produtoMapper.toEntity(request, empresa)).thenReturn(produtoEntidade);
        when(produtoRepository.save(produtoEntidade)).thenReturn(produtoEntidade);
        when(produtoMapper.toResponse(produtoEntidade)).thenReturn(responseEsperado);

        ProdutoResponse responseRecebido = produtoService.criarProduto(request);

        assertNotNull(responseRecebido);
        assertEquals(responseEsperado.id(), responseRecebido.id());
        verify(produtoRepository, times(1)).save(produtoEntidade);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com empresa inexistente")
    void deveLancarErroQuandoEmpresaNaoExistir() {
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> produtoService.criarProduto(request));
        verify(produtoRepository, never()).save(any());
    }


    @Test
    @DisplayName("Deve buscar um produto por ID com sucesso")
    void deveBuscarProdutoPorId() {
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoEntidade));
        when(produtoMapper.toResponse(produtoEntidade)).thenReturn(responseEsperado);

        ProdutoResponse response = produtoService.buscarPorId(produtoId);

        assertNotNull(response);
        assertEquals(produtoId, response.id());
    }

    @Test
    @DisplayName("Deve listar produtos ativos com paginação")
    void deveListarProdutosAtivos() {
        Pageable pageable = PageRequest.of(0, 10);
        when(produtoRepository.findAllByAtivoTrue(pageable)).thenReturn(new PageImpl<>(List.of(produtoEntidade)));
        when(produtoMapper.toResponse(produtoEntidade)).thenReturn(responseEsperado);

        Page<ProdutoResponse> resultado = produtoService.listarTodos(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(produtoRepository, times(1)).findAllByAtivoTrue(pageable);
    }


    @Test
    @DisplayName("Deve atualizar o preço e o nome de um produto com sucesso")
    void deveAtualizarProdutoComSucesso() {
        ProdutoAtualizacaoRequest atualizacaoRequest = new ProdutoAtualizacaoRequest("Teclado Gamer", new BigDecimal("400.00"), null, null, null);

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoEntidade));
        when(produtoMapper.toResponse(produtoEntidade)).thenReturn(responseEsperado);

        ProdutoResponse response = produtoService.atualizar(produtoId, atualizacaoRequest);

        assertNotNull(response);
        assertEquals("Teclado Gamer", produtoEntidade.getNome());
        assertEquals(new BigDecimal("400.00"), produtoEntidade.getPreco());
    }


    @Test
    @DisplayName("Deve inativar um produto com sucesso")
    void deveInativarProduto() {
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoEntidade));

        produtoService.inativar(produtoId);

        verify(produtoRepository, times(1)).findById(produtoId);
    }

}