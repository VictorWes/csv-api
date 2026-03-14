package com.csv.mapper;

import com.csv.controller.request.VendaRequest;
import com.csv.controller.response.VendaResponse;
import com.csv.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VendaMapperTest {

    private VendaMapper vendaMapper;
    private Empresa empresa;
    private FormaPagamento formaPagamento;
    private UUID empresaId;
    private UUID formaPagamentoId;

    @BeforeEach
    void setUp() {
        vendaMapper = new VendaMapper();

        empresaId = UUID.randomUUID();
        empresa = new Empresa();
        empresa.setId(empresaId);

        formaPagamentoId = UUID.randomUUID();
        formaPagamento = new FormaPagamento();
        formaPagamento.setId(formaPagamentoId);
    }

    @Test
    @DisplayName("Deve converter VendaRequest em Entidade Venda (sem cliente e vendedor)")
    void deveConverterRequestParaEntity() {
        VendaRequest request = new VendaRequest(empresaId, formaPagamentoId, null, null);

        Venda entity = vendaMapper.toEntity(request, empresa, formaPagamento, null, null);

        assertNotNull(entity);
        assertEquals(empresa, entity.getEmpresa());
        assertEquals(formaPagamento, entity.getFormaPagamento());
        assertNull(entity.getCliente());
        assertNull(entity.getVendedor());
        assertEquals(BigDecimal.ZERO, entity.getValorTotal()); // Deve inicializar com zero
    }

    @Test
    @DisplayName("Deve converter Entidade Venda em VendaResponse com total calculado")
    void deveConverterEntityParaResponse() {
        Venda venda = new Venda();
        venda.setId(UUID.randomUUID());
        venda.setEmpresa(empresa);
        venda.setFormaPagamento(formaPagamento);
        venda.setValorTotal(new BigDecimal("150.00"));
        venda.setAtivo(true);

        VendaResponse response = vendaMapper.toResponse(venda);

        assertNotNull(response);
        assertEquals(venda.getId(), response.id());
        assertEquals(empresaId, response.empresaId());
        assertEquals(formaPagamentoId, response.formaPagamentoId());
        assertEquals(new BigDecimal("150.00"), response.valorTotal());
        assertTrue(response.ativo());
    }
}