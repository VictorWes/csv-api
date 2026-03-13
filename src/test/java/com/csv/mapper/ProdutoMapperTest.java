package com.csv.mapper;

import com.csv.controller.request.ProdutoRequest;
import com.csv.controller.response.ProdutoResponse;
import com.csv.entities.Empresa;
import com.csv.entities.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProdutoMapperTest {

    private ProdutoMapper produtoMapper;
    private Empresa empresa;
    private UUID empresaId;

    @BeforeEach
    void setUp() {
        produtoMapper = new ProdutoMapper();

        empresaId = UUID.randomUUID();
        empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNome("Loja Matriz");
    }

    @Test
    @DisplayName("Deve converter ProdutoRequest em uma entidade Produto")
    void deveConverterRequestParaEntity() {
        // ARRANGE
        ProdutoRequest request = new ProdutoRequest(
                "Teclado Mecânico",
                new BigDecimal("350.00"),
                "Periféricos",
                "http://foto.com/teclado.png",
                "789123456",
                empresaId
        );

        // ACT
        Produto entity = produtoMapper.toEntity(request, empresa);

        // ASSERT
        assertNotNull(entity);
        assertEquals(request.nome(), entity.getNome());
        assertEquals(request.preco(), entity.getPreco());
        assertEquals(request.segmento(), entity.getSegmento());
        assertEquals(request.urlFoto(), entity.getUrlFoto());
        assertEquals(request.codigoBarras(), entity.getCodigoBarras());
        assertEquals(empresa, entity.getEmpresa());
    }

    @Test
    @DisplayName("Deve converter entidade Produto em ProdutoResponse")
    void deveConverterEntityParaResponse() {
        // ARRANGE
        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setNome("Mouse Gamer");
        produto.setPreco(new BigDecimal("150.00"));
        produto.setSegmento("Periféricos");
        produto.setUrlFoto("http://foto.com/mouse.png");
        produto.setCodigoBarras("123456789");
        produto.setEmpresa(empresa);

        // ACT
        ProdutoResponse response = produtoMapper.toResponse(produto);

        // ASSERT
        assertNotNull(response);
        assertEquals(produto.getId(), response.id());
        assertEquals(produto.getNome(), response.nome());
        assertEquals(produto.getPreco(), response.preco());
        assertEquals(produto.getSegmento(), response.segmento());
        assertEquals(produto.getUrlFoto(), response.urlFoto());
        assertEquals(produto.getCodigoBarras(), response.codigoBarras());
        assertEquals(empresa.getId(), response.empresaId());
    }
}
