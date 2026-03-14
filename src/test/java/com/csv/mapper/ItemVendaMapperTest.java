package com.csv.mapper;

import com.csv.controller.request.ItemVendaRequest;
import com.csv.controller.response.ItemVendaResponse;
import com.csv.entities.ItemVenda;
import com.csv.entities.Produto;
import com.csv.entities.Venda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ItemVendaMapperTest {

    private ItemVendaMapper itemVendaMapper;
    private Venda venda;
    private Produto produto;
    private UUID vendaId;
    private UUID produtoId;

    @BeforeEach
    void setUp() {
        itemVendaMapper = new ItemVendaMapper();

        vendaId = UUID.randomUUID();
        venda = new Venda();
        venda.setId(vendaId);

        produtoId = UUID.randomUUID();
        produto = new Produto();
        produto.setId(produtoId);
        produto.setNome("Teclado Mecânico");
    }

    @Test
    @DisplayName("Deve converter ItemVendaRequest em entidade ItemVenda")
    void deveConverterRequestParaEntity() {
        ItemVendaRequest request = new ItemVendaRequest(
                2,
                new BigDecimal("150.00"),
                vendaId,
                produtoId
        );

        ItemVenda entity = itemVendaMapper.toEntity(request, venda, produto);

        assertNotNull(entity);
        assertEquals(request.quantidade(), entity.getQuantidade());
        assertEquals(request.precoUnitario(), entity.getPrecoUnitario());
        assertEquals(venda, entity.getVenda());
        assertEquals(produto, entity.getProduto());
    }

    @Test
    @DisplayName("Deve converter entidade ItemVenda em ItemVendaResponse calculando subtotal")
    void deveConverterEntityParaResponse() {
        ItemVenda itemVenda = new ItemVenda();
        itemVenda.setId(UUID.randomUUID());
        itemVenda.setQuantidade(3);
        itemVenda.setPrecoUnitario(new BigDecimal("100.00"));
        itemVenda.setVenda(venda);
        itemVenda.setProduto(produto);

        ItemVendaResponse response = itemVendaMapper.toResponse(itemVenda);

        assertNotNull(response);
        assertEquals(itemVenda.getId(), response.id());
        assertEquals(3, response.quantidade());
        assertEquals(new BigDecimal("100.00"), response.precoUnitario());
        assertEquals(new BigDecimal("300.00"), response.subtotal());
        assertEquals(vendaId, response.vendaId());
        assertEquals(produtoId, response.produtoId());
        assertEquals("Teclado Mecânico", response.nomeProduto());
    }

}