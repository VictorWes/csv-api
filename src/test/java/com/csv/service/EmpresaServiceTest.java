package com.csv.service;

import com.csv.controller.request.EmpresaAtualizacaoRequest;
import com.csv.controller.request.EmpresaRequest;
import com.csv.controller.response.EmpresaResponse;
import com.csv.entities.Empresa;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.EmpresaMapper;
import com.csv.repository.EmpresaRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
    private Empresa empresaEntidade;
    private EmpresaResponse responseEsperado;
    private UUID empresaId;

    @BeforeEach
    void setUp() {
        empresaId = UUID.randomUUID();
        request = new EmpresaRequest("Empresa Teste", "http://logo.url/logo.png");

        empresaEntidade = new Empresa();
        empresaEntidade.setId(empresaId);
        empresaEntidade.setNome(request.nome());
        empresaEntidade.setUrlLogo(request.urlLogo());
        responseEsperado = new EmpresaResponse(empresaId, empresaEntidade.getNome(), empresaEntidade.getUrlLogo(), LocalDateTime.now());
    }



    @Test
    @DisplayName("Deve criar uma empresa com sucesso")
    void deveCriarEmpresaComSucesso() {
        when(empresaMapper.toEntity(request)).thenReturn(empresaEntidade);
        when(empresaRepository.save(empresaEntidade)).thenReturn(empresaEntidade);
        when(empresaMapper.toResponse(empresaEntidade)).thenReturn(responseEsperado);

        EmpresaResponse responseRecebido = empresaService.criarEmpresa(request);

        assertNotNull(responseRecebido);
        assertEquals(responseEsperado, responseRecebido);
        verify(empresaRepository, times(1)).save(empresaEntidade);
    }



    @Test
    @DisplayName("Deve buscar uma empresa por ID com sucesso")
    void deveBuscarEmpresaPorId() {
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresaEntidade));
        when(empresaMapper.toResponse(empresaEntidade)).thenReturn(responseEsperado);

        EmpresaResponse responseRecebido = empresaService.buscarPorId(empresaId);

        assertNotNull(responseRecebido);
        assertEquals(empresaId, responseRecebido.id());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar empresa com ID inexistente")
    void deveLancarExcecaoAoBuscarEmpresaInexistente() {
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> empresaService.buscarPorId(empresaId));
    }

    @Test
    @DisplayName("Deve listar empresas ativas com sucesso")
    void deveListarEmpresasAtivas() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Empresa> pagina = new PageImpl<>(List.of(empresaEntidade));

        when(empresaRepository.findAllByAtivoTrue(pageable)).thenReturn(pagina);
        when(empresaMapper.toResponse(empresaEntidade)).thenReturn(responseEsperado);

        Page<EmpresaResponse> resultado = empresaService.listarTodas(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(empresaRepository, times(1)).findAllByAtivoTrue(pageable);
    }

    @Test
    @DisplayName("Deve listar empresas inativas com sucesso")
    void deveListarEmpresasInativas() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Empresa> pagina = new PageImpl<>(List.of(empresaEntidade));

        when(empresaRepository.findAllByAtivoFalse(pageable)).thenReturn(pagina);
        when(empresaMapper.toResponse(empresaEntidade)).thenReturn(responseEsperado);

        Page<EmpresaResponse> resultado = empresaService.listarInativas(pageable);

        assertNotNull(resultado);
        verify(empresaRepository, times(1)).findAllByAtivoFalse(pageable);
    }


    @Test
    @DisplayName("Deve atualizar uma empresa com sucesso")
    void deveAtualizarEmpresaComSucesso() {
        EmpresaAtualizacaoRequest atualizacaoRequest = new EmpresaAtualizacaoRequest("Novo Nome", null);

        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresaEntidade));
        when(empresaMapper.toResponse(empresaEntidade)).thenReturn(responseEsperado);

        EmpresaResponse responseRecebido = empresaService.atualizar(empresaId, atualizacaoRequest);

        assertNotNull(responseRecebido);
        assertEquals("Novo Nome", empresaEntidade.getNome()); // Valida o Domínio Rico
        verify(empresaMapper, times(1)).toResponse(empresaEntidade);
    }


    @Test
    @DisplayName("Deve inativar uma empresa com sucesso")
    void deveInativarEmpresaComSucesso() {
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresaEntidade));

        empresaService.inativar(empresaId);

        verify(empresaRepository, times(1)).findById(empresaId);
    }
}
