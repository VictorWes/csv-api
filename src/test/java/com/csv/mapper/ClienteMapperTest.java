package com.csv.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.csv.controller.request.ClienteRequest;
import com.csv.controller.response.ClienteResponse;
import com.csv.entities.Cliente;
import com.csv.entities.Empresa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;


class ClienteMapperTest {

    private ClienteMapper clienteMapper;

    private UUID empresaId;
    private UUID clienteId;
    private LocalDate dataNascimento;

    private Empresa empresa;
    private ClienteRequest request;
    private Cliente clienteEntidade;

    @BeforeEach
    void setUp() {
        clienteMapper = new ClienteMapper();

        empresaId = UUID.randomUUID();
        clienteId = UUID.randomUUID();
        dataNascimento = LocalDate.of(1990, 5, 15);
        empresa = new Empresa();
        empresa.setId(empresaId);

        request = new ClienteRequest("João Silva", "joao@email.com", "11999999999", dataNascimento, empresaId);


        clienteEntidade = new Cliente();
        clienteEntidade.setId(clienteId);
        clienteEntidade.setNome("João Silva");
        clienteEntidade.setEmail("joao@email.com");
        clienteEntidade.setTelefone("11999999999");
        clienteEntidade.setDataNascimento(dataNascimento);
        clienteEntidade.setEmpresa(empresa);
    }

    @Test
    @DisplayName("Deve converter ClienteRequest para Entidade Cliente corretamente")
    void deveConverterRequestParaEntidade() {
        // ACT
        Cliente entidadeConvertida = clienteMapper.toEntity(request, empresa);

        // ASSERT
        assertNotNull(entidadeConvertida);
        assertEquals(request.nome(), entidadeConvertida.getNome());
        assertEquals(request.email(), entidadeConvertida.getEmail());
        assertEquals(request.telefone(), entidadeConvertida.getTelefone());
        assertEquals(request.dataNascimento(), entidadeConvertida.getDataNascimento());

        assertNotNull(entidadeConvertida.getEmpresa());
        assertEquals(empresa.getId(), entidadeConvertida.getEmpresa().getId());
    }

    @Test
    @DisplayName("Deve converter Entidade Cliente para ClienteResponse corretamente")
    void deveConverterEntidadeParaResponse() {
        // ACT
        ClienteResponse responseConvertida = clienteMapper.toResponse(clienteEntidade);

        // ASSERT
        assertNotNull(responseConvertida);
        assertEquals(clienteEntidade.getId(), responseConvertida.id());
        assertEquals(clienteEntidade.getNome(), responseConvertida.nome());
        assertEquals(clienteEntidade.getEmail(), responseConvertida.email());
        assertEquals(clienteEntidade.getTelefone(), responseConvertida.telefone());
        assertEquals(clienteEntidade.getDataNascimento(), responseConvertida.dataNascimento());

        assertEquals(clienteEntidade.getEmpresa().getId(), responseConvertida.empresaId());
    }

}