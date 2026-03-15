package com.csv.mapper;

import com.csv.controller.request.VendedorRequest;
import com.csv.controller.response.VendedorResponse;
import com.csv.entities.Empresa;
import com.csv.entities.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VendedorMapperTest {

    private VendedorMapper vendedorMapper;
    private Empresa empresa;
    private UUID empresaId;

    @BeforeEach
    void setUp() {
        vendedorMapper = new VendedorMapper();

        empresaId = UUID.randomUUID();
        empresa = new Empresa();
        empresa.setId(empresaId);
    }

    @Test
    @DisplayName("Deve converter VendedorRequest para Entidade Vendedor com Empresa")
    void deveConverterRequestParaEntity() {
        VendedorRequest request = new VendedorRequest(
                "João Silva",
                "Vendedor Sênior",
                LocalDate.of(1990, 5, 15),
                empresaId
        );

        Vendedor entity = vendedorMapper.toEntity(request, empresa);

        assertNotNull(entity);
        assertEquals("João Silva", entity.getNome());
        assertEquals("Vendedor Sênior", entity.getCargo());
        assertEquals(LocalDate.of(1990, 5, 15), entity.getDataNascimento());
        assertEquals(empresa, entity.getEmpresa());
    }

    @Test
    @DisplayName("Deve converter Entidade Vendedor para VendedorResponse")
    void deveConverterEntityParaResponse() {
        Vendedor vendedor = new Vendedor();
        vendedor.setId(UUID.randomUUID());
        vendedor.setNome("Maria Souza");
        vendedor.setCargo("Gerente de Vendas");
        vendedor.setDataNascimento(LocalDate.of(1985, 10, 20));
        vendedor.setEmpresa(empresa);
        vendedor.setAtivo(true);

        VendedorResponse response = vendedorMapper.toResponse(vendedor);

        assertNotNull(response);
        assertEquals(vendedor.getId(), response.id());
        assertEquals("Maria Souza", response.nome());
        assertEquals("Gerente de Vendas", response.cargo());
        assertEquals(empresaId, response.empresaId());
        assertTrue(response.ativo());
    }
}