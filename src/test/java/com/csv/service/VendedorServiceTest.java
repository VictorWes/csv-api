package com.csv.service;

import com.csv.controller.request.VendedorAtualizacaoRequest;
import com.csv.controller.request.VendedorRequest;
import com.csv.controller.response.VendedorResponse;
import com.csv.entities.Empresa;
import com.csv.entities.Vendedor;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.VendedorMapper;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.VendedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendedorServiceTest {

    @InjectMocks private VendedorService vendedorService;

    @Mock private VendedorRepository vendedorRepository;
    @Mock private EmpresaRepository empresaRepository;
    @Mock private VendedorMapper vendedorMapper;

    private UUID vendedorId;
    private UUID empresaId;
    private Empresa empresa;
    private Vendedor vendedorEntidade;
    private VendedorResponse vendedorResponse;

    @BeforeEach
    void setup() {
        vendedorId = UUID.randomUUID();
        empresaId = UUID.randomUUID();

        empresa = new Empresa();
        empresa.setId(empresaId);

        vendedorEntidade = new Vendedor();
        vendedorEntidade.setId(vendedorId);
        vendedorEntidade.setNome("Carlos");
        vendedorEntidade.setEmpresa(empresa);
        vendedorEntidade.setAtivo(true);

        vendedorResponse = new VendedorResponse(vendedorId, "Carlos", null, null, empresaId, true);
    }

    @Test
    @DisplayName("Deve criar um vendedor com sucesso")
    void deveCriarVendedor() {
        VendedorRequest request = new VendedorRequest("Carlos", null, null, empresaId);

        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(vendedorMapper.toEntity(request, empresa)).thenReturn(vendedorEntidade);
        when(vendedorRepository.save(vendedorEntidade)).thenReturn(vendedorEntidade);
        when(vendedorMapper.toResponse(vendedorEntidade)).thenReturn(vendedorResponse);

        VendedorResponse response = vendedorService.criar(request);

        assertNotNull(response);
        assertEquals("Carlos", response.nome());
        verify(vendedorRepository, times(1)).save(vendedorEntidade);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar vendedor com empresa inexistente")
    void deveLancarExcecaoEmpresaNaoEncontrada() {
        VendedorRequest request = new VendedorRequest("Carlos", null, null, empresaId);

        when(empresaRepository.findById(empresaId)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> vendedorService.criar(request));
        verify(vendedorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar os dados do vendedor")
    void deveAtualizarVendedor() {
        VendedorAtualizacaoRequest request = new VendedorAtualizacaoRequest("Carlos Atualizado", "Gerente", LocalDate.of(1980, 1, 1));

        when(vendedorRepository.findById(vendedorId)).thenReturn(Optional.of(vendedorEntidade));
        when(vendedorMapper.toResponse(vendedorEntidade)).thenReturn(new VendedorResponse(vendedorId, "Carlos Atualizado", "Gerente", LocalDate.of(1980, 1, 1), empresaId, true));

        VendedorResponse response = vendedorService.atualizar(vendedorId, request);

        assertEquals("Carlos Atualizado", vendedorEntidade.getNome());
        assertEquals("Gerente", vendedorEntidade.getCargo());
    }

    @Test
    @DisplayName("Deve inativar o vendedor com sucesso")
    void deveInativarVendedor() {
        when(vendedorRepository.findById(vendedorId)).thenReturn(Optional.of(vendedorEntidade));

        vendedorService.inativar(vendedorId);

        assertFalse(vendedorEntidade.getAtivo());
    }
}