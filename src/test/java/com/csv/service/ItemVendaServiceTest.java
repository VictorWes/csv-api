package com.csv.service;

import com.csv.controller.request.ItemVendaAtualizacaoRequest;
import com.csv.controller.request.ItemVendaRequest;
import com.csv.controller.response.ItemVendaResponse;
import com.csv.entities.ItemVenda;
import com.csv.entities.Produto;
import com.csv.entities.Venda;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.ItemVendaMapper;
import com.csv.repository.ItemVendaRepository;
import com.csv.repository.ProdutoRepository;
import com.csv.repository.VendaRepository;
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
class ItemVendaServiceTest {

    @InjectMocks private ItemVendaService itemVendaService;
    @Mock private ItemVendaRepository itemVendaRepository;
    @Mock private VendaRepository vendaRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private ItemVendaMapper itemVendaMapper;

    private UUID itemVendaId;
    private UUID vendaId;
    private UUID produtoId;
    private ItemVendaRequest request;
    private Venda venda;
    private Produto produto;
    private ItemVenda itemVendaEntidade;
    private ItemVendaResponse responseEsperado;

    @BeforeEach
    void setup() {
        itemVendaId = UUID.randomUUID();
        vendaId = UUID.randomUUID();
        produtoId = UUID.randomUUID();

        venda = new Venda();
        venda.setId(vendaId);

        produto = new Produto();
        produto.setId(produtoId);
        produto.setNome("Mouse");

        request = new ItemVendaRequest(2, new BigDecimal("50.00"), vendaId, produtoId);

        itemVendaEntidade = new ItemVenda();
        itemVendaEntidade.setId(itemVendaId);
        itemVendaEntidade.setQuantidade(request.quantidade());
        itemVendaEntidade.setPrecoUnitario(request.precoUnitario());
        itemVendaEntidade.setVenda(venda);
        itemVendaEntidade.setProduto(produto);

        responseEsperado = new ItemVendaResponse(itemVendaId, 2, new BigDecimal("50.00"), new BigDecimal("100.00"), vendaId, produtoId, "Mouse");
    }

    @Test
    @DisplayName("Deve adicionar item na venda com sucesso")
    void deveAdicionarItemComSucesso() {
        when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
        when(itemVendaMapper.toEntity(request, venda, produto)).thenReturn(itemVendaEntidade);
        when(itemVendaRepository.save(itemVendaEntidade)).thenReturn(itemVendaEntidade);
        when(itemVendaMapper.toResponse(itemVendaEntidade)).thenReturn(responseEsperado);

        ItemVendaResponse response = itemVendaService.adicionarItem(request);

        assertNotNull(response);
        assertEquals(responseEsperado.id(), response.id());
        verify(itemVendaRepository, times(1)).save(itemVendaEntidade);
    }

    @Test
    @DisplayName("Deve listar itens filtrando por ID da Venda")
    void deveListarPorVenda() {
        Pageable pageable = PageRequest.of(0, 10);
        when(itemVendaRepository.findAllByVendaIdAndAtivoTrue(vendaId, pageable)).thenReturn(new PageImpl<>(List.of(itemVendaEntidade)));
        when(itemVendaMapper.toResponse(itemVendaEntidade)).thenReturn(responseEsperado);

        Page<ItemVendaResponse> resultado = itemVendaService.listarPorVenda(vendaId, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(itemVendaRepository).findAllByVendaIdAndAtivoTrue(vendaId, pageable);
    }

    @Test
    @DisplayName("Deve atualizar a quantidade de um item")
    void deveAtualizarQuantidade() {
        ItemVendaAtualizacaoRequest attRequest = new ItemVendaAtualizacaoRequest(5);

        when(itemVendaRepository.findById(itemVendaId)).thenReturn(Optional.of(itemVendaEntidade));
        when(itemVendaMapper.toResponse(itemVendaEntidade)).thenReturn(responseEsperado);

        itemVendaService.atualizarQuantidade(itemVendaId, attRequest);

        assertEquals(5, itemVendaEntidade.getQuantidade()); // Valida o domínio rico
    }

    @Test
    @DisplayName("Deve inativar um item da venda")
    void deveRemoverItem() {
        when(itemVendaRepository.findById(itemVendaId)).thenReturn(Optional.of(itemVendaEntidade));
        itemVendaService.removerItem(itemVendaId);
        verify(itemVendaRepository).findById(itemVendaId);
    }
}