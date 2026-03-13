package com.csv.service;

import com.csv.controller.request.ClienteAtualizacaoRequest;
import com.csv.controller.request.ClienteRequest;
import com.csv.controller.response.ClienteResponse;
import com.csv.entities.Cliente;
import com.csv.entities.Empresa;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.ClienteMapper;
import com.csv.repository.ClienteRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock private ClienteRepository clienteRepository;
    @Mock private EmpresaRepository empresaRepository;
    @Mock private ClienteMapper clienteMapper;

    private UUID empresaId;
    private UUID clienteId;
    private ClienteRequest request;
    private Empresa empresaMock;
    private Cliente clienteEntidade;
    private ClienteResponse responseEsperada;

    @BeforeEach
    void setUp() {
        empresaId = UUID.randomUUID();
        clienteId = UUID.randomUUID();
        LocalDate dataNascimento = LocalDate.of(1990, 5, 15);

        request = new ClienteRequest("João Silva", "joao@email.com", "11999999999", dataNascimento, empresaId);

        empresaMock = new Empresa();
        empresaMock.setId(empresaId);

        clienteEntidade = new Cliente();
        clienteEntidade.setId(clienteId);
        clienteEntidade.setNome("João Silva");

        responseEsperada = new ClienteResponse(clienteId, "João Silva", "joao@email.com", "11999999999", dataNascimento, empresaId);
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso quando a empresa existir")
    void deveCriarClienteComSucesso() {
        // ARRANGE
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresaMock));
        when(clienteMapper.toEntity(request, empresaMock)).thenReturn(clienteEntidade);
        when(clienteRepository.save(clienteEntidade)).thenReturn(clienteEntidade);
        when(clienteMapper.toResponse(clienteEntidade)).thenReturn(responseEsperada);

        // ACT
        ClienteResponse responseAtual = clienteService.criarCliente(request);

        // ASSERT
        assertNotNull(responseAtual);
        assertEquals(responseEsperada.id(), responseAtual.id());
        assertEquals(responseEsperada.nome(), responseAtual.nome());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando empresa não existir")
    void deveLancarExcecaoQuandoEmpresaNaoEncontrada() {
        // ARRANGE
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            clienteService.criarCliente(request);
        });

        assertEquals("Empresa não encontrada pelo ID informado.", exception.getMessage());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar cliente com e-mail já existente")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        when(clienteRepository.existsByEmail(request.email())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.criarCliente(request);
        });

        assertEquals("Já existe um cliente cadastrado com este e-mail.", exception.getMessage());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar cliente por ID com sucesso")
    void deveBuscarClientePorIdComSucesso() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteEntidade));
        when(clienteMapper.toResponse(clienteEntidade)).thenReturn(responseEsperada);

        ClienteResponse responseAtual = clienteService.buscarPorId(clienteId);

        assertNotNull(responseAtual);
        assertEquals(clienteId, responseAtual.id());
    }

    @Test
    @DisplayName("Deve listar todos os clientes ativos paginados")
    void deveListarClientesAtivos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> paginaClientes = new PageImpl<>(List.of(clienteEntidade));

        when(clienteRepository.findAllByAtivoTrue(pageable)).thenReturn(paginaClientes);
        when(clienteMapper.toResponse(clienteEntidade)).thenReturn(responseEsperada);

        Page<ClienteResponse> resultado = clienteService.listarTodos(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(clienteRepository, times(1)).findAllByAtivoTrue(pageable);
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        ClienteAtualizacaoRequest atualizacaoRequest = new ClienteAtualizacaoRequest("Nome Novo", null, null, null);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteEntidade));
        when(clienteMapper.toResponse(clienteEntidade)).thenReturn(responseEsperada);

        ClienteResponse responseAtual = clienteService.atualizar(clienteId, atualizacaoRequest);

        assertNotNull(responseAtual);
        assertEquals("Nome Novo", clienteEntidade.getNome());
        verify(clienteMapper, times(1)).toResponse(clienteEntidade);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar para um email já pertencente a outro cliente")
    void deveLancarExcecaoAoAtualizarEmailJaExistente() {
        ClienteAtualizacaoRequest atualizacaoRequest = new ClienteAtualizacaoRequest(null, "email.do.hacker@email.com", null, null);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteEntidade));
        when(clienteRepository.existsByEmail("email.do.hacker@email.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.atualizar(clienteId, atualizacaoRequest);
        });

        assertEquals("Já existe um cliente cadastrado com este e-mail.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve inativar cliente com sucesso (Soft Delete)")
    void deveInativarClienteComSucesso() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteEntidade));

        clienteService.inativar(clienteId);
        verify(clienteRepository, times(1)).findById(clienteId);
    }

    @Test
    @DisplayName("Deve listar clientes inativos paginados")
    void deveListarClientesInativos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> paginaClientes = new PageImpl<>(List.of(clienteEntidade));

        when(clienteRepository.findAllByAtivoFalse(pageable)).thenReturn(paginaClientes);
        when(clienteMapper.toResponse(clienteEntidade)).thenReturn(responseEsperada);

        Page<ClienteResponse> resultado = clienteService.listarInativos(pageable);

        assertNotNull(resultado);
        verify(clienteRepository, times(1)).findAllByAtivoFalse(pageable);
    }

}