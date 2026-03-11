package com.csv.mapper;

import com.csv.controller.request.EmpresaRequest;
import com.csv.controller.response.EmpresaResponse;
import com.csv.entities.Empresa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmpresaMapperTest {

    // O nosso ator principal, sem depender de dublês (Mocks)
    private EmpresaMapper empresaMapper;

    @BeforeEach
    void setUp() {
        empresaMapper = new EmpresaMapper();
    }

    @Test
    @DisplayName("Deve converter EmpresaRequest em uma entidade Empresa")
    void deveConverterRequestParaEntity() {
        // ARRANGE - Os dados brutos chegando da requisição
        EmpresaRequest request = new EmpresaRequest(
                "Giga+",
                "http://logo.com/gigas.png"
        );

        // ACT - A mágica da conversão
        Empresa entity = empresaMapper.toEntity(request);

        // ASSERT - Garantindo que nada se perdeu na tradução
        assertNotNull(entity);
        assertEquals(request.nome(), entity.getNome());
        assertEquals(request.urlLogo(), entity.getUrlLogo());
    }

    @Test
    @DisplayName("Deve converter entidade Empresa em EmpresaResponse")
    void deveConverterEntityParaResponse() {
        // ARRANGE - A entidade completa vindo do banco de dados
        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        empresa.setNome("Ferraria de Valfenda");
        empresa.setUrlLogo("http://logo.com/valfenda.png");
        // Se a sua Entidade tiver o campo dataCriacao, você pode setar aqui também!

        // ACT - A conversão para mostrar ao mundo exterior
        EmpresaResponse response = empresaMapper.toResponse(empresa);

        // ASSERT
        assertNotNull(response);
        assertEquals(empresa.getId(), response.id());
        assertEquals(empresa.getNome(), response.nome());
        assertEquals(empresa.getUrlLogo(), response.urlLogo());
    }

}