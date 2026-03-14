package com.csv.mapper;

import com.csv.controller.request.FormaPagamentoRequest;
import com.csv.controller.response.FormaPagamentoResponse;
import com.csv.entities.Empresa;
import com.csv.entities.FormaPagamento;
import com.csv.enums.TipoBasePagamentoEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FormaPagamentoMapperTest {

    private FormaPagamentoMapper formaPagamentoMapper;
    private Empresa empresa;
    private UUID empresaId;

    @BeforeEach
    void setUp() {
        formaPagamentoMapper = new FormaPagamentoMapper();

        empresaId = UUID.randomUUID();
        empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNome("Loja Matriz CSV");
    }

    @Test
    @DisplayName("Deve converter FormaPagamentoRequest em uma entidade FormaPagamento")
    void deveConverterRequestParaEntity() {
        // ARRANGE
        FormaPagamentoRequest request = new FormaPagamentoRequest(
                "PIX",
                TipoBasePagamentoEnum.PIX,
                empresaId
        );

        // ACT
        FormaPagamento entity = formaPagamentoMapper.toEntity(request, empresa);

        // ASSERT
        assertNotNull(entity);
        assertEquals(request.nome(), entity.getNome());
        assertEquals(request.tipoBase(), entity.getTipoBase());
        assertEquals(empresa, entity.getEmpresa());
    }

    @Test
    @DisplayName("Deve converter entidade FormaPagamento em FormaPagamentoResponse")
    void deveConverterEntityParaResponse() {
        // ARRANGE
        FormaPagamento formaPagamento = new FormaPagamento();
        formaPagamento.setId(UUID.randomUUID());
        formaPagamento.setNome("Pix");
        formaPagamento.setTipoBase(TipoBasePagamentoEnum.PIX);
        formaPagamento.setEmpresa(empresa);

        // ACT
        FormaPagamentoResponse response = formaPagamentoMapper.toResponse(formaPagamento);

        // ASSERT
        assertNotNull(response);
        assertEquals(formaPagamento.getId(), response.id());
        assertEquals(formaPagamento.getNome(), response.nome());
        assertEquals(formaPagamento.getTipoBase(), response.tipoBase());
        assertEquals(empresa.getId(), response.empresaId());
    }
}