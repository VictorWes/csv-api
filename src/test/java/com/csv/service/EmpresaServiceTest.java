package com.csv.service;

import com.csv.controller.request.EmpresaRequest;
import com.csv.controller.response.EmpresaResponse;
import com.csv.entities.Empresa;
import com.csv.mapper.EmpresaMapper;
import com.csv.repository.EmpresaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @InjectMocks
    private EmpresaService empresaService;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EmpresaMapper empresaMapper;

    private EmpresaRequest request;
    private Empresa empresaMapeada;
    private Empresa empresaSalva;
    private EmpresaResponse responseEsperado;

    @BeforeEach
    void setUp() {
        request = new EmpresaRequest("Empresa Teste", "http://logo.url/logo.png");

        empresaMapeada = new Empresa();
        empresaMapeada.setNome(request.nome());
        empresaMapeada.setUrlLogo(request.urlLogo());

        empresaSalva = new Empresa();
        empresaSalva.setId(UUID.randomUUID());
        empresaSalva.setNome(request.nome());
        empresaSalva.setUrlLogo(request.urlLogo());

        responseEsperado = new EmpresaResponse(empresaSalva.getId(), empresaSalva.getNome(), empresaSalva.getUrlLogo(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve criar uma empresa com sucesso")
    void deveCriarEmpresaComSucesso() {
        // ARRANGE
        when(empresaMapper.toEntity(request)).thenReturn(empresaMapeada);
        when(empresaRepository.save(empresaMapeada)).thenReturn(empresaSalva);
        when(empresaMapper.toResponse(empresaSalva)).thenReturn(responseEsperado);

        // ACT
        EmpresaResponse responseRecebido = empresaService.criarEmpresa(request);

        // ASSERT
        assertNotNull(responseRecebido);
        assertEquals(responseEsperado, responseRecebido);

        // VERIFY
        verify(empresaMapper, times(1)).toEntity(request);
        verify(empresaRepository, times(1)).save(empresaMapeada);
        verify(empresaMapper, times(1)).toResponse(empresaSalva);
    }
}
