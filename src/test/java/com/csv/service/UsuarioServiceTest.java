package com.csv.service;

import com.csv.controller.request.UsuarioRequest;
import com.csv.controller.response.UsuarioResponse;
import com.csv.entities.Empresa;
import com.csv.entities.Usuario;
import com.csv.enums.PerfilEnum;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.infra.exception.RegraNegocioException;
import com.csv.mapper.UsuarioMapper;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioRequest request;
    private Empresa empresa;
    private UUID empresaId;

    @BeforeEach
    void setup() {
        empresaId = UUID.randomUUID();
        empresa = new Empresa();
        empresa.setId(empresaId);

        request = new UsuarioRequest(
                "Victor",
                "victor@gmail.com",
                "senha123",
                empresaId,
                PerfilEnum.ADMIN
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando tentar criar usuário com e-mail já existente")
    void deveLancarErroQuandoEmailJaExistir() {
        // ARRANGE
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(new Usuario()));

        // ACT & ASSERT
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            usuarioService.criarUsuario(request);
        });

        assertEquals("Já existe um usuário cadastrado com este e-mail.", exception.getMessage());

        // VERIFY
        verify(usuarioRepository, times(1)).findByEmail(anyString());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a empresa não for encontrada pelo ID")
    void deveLancarErroQuandoEmpresaNaoExistir() {
        // ARRANGE
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(empresaRepository.findById(request.empresaId())).thenReturn(Optional.empty());

        // ACT & ASSERT
        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            usuarioService.criarUsuario(request);
        });

        assertEquals("Empresa não encontrada pelo ID informado.", exception.getMessage());

        // VERIFY
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso (caminho feliz)")
    void deveCriarUsuarioComSucesso() {

        // ARRANGE
        var usuarioMapeado = new Usuario();
        usuarioMapeado.setEmail(request.email());

        var senhaCriptografada = "senha_super_criptografada";

        var usuarioSalvo = new Usuario();
        usuarioSalvo.setId(UUID.randomUUID());
        usuarioSalvo.setNome(request.nome());
        usuarioSalvo.setEmail(request.email());
        usuarioSalvo.setPerfil(PerfilEnum.ADMIN);

        var responseEsperado = new UsuarioResponse(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getPerfil().name(),
                empresaId
        );


        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(empresaRepository.findById(request.empresaId())).thenReturn(Optional.of(empresa));
        when(usuarioMapper.toEntity(request, empresa)).thenReturn(usuarioMapeado);
        when(passwordEncoder.encode(request.senha())).thenReturn(senhaCriptografada);
        when(usuarioRepository.save(usuarioMapeado)).thenReturn(usuarioSalvo);
        when(usuarioMapper.toResponse(usuarioSalvo)).thenReturn(responseEsperado);

        // ACT
        var responseRecebido = usuarioService.criarUsuario(request);

        // ASSERT
        assertNotNull(responseRecebido);
        assertEquals(responseEsperado.id(), responseRecebido.id());
        assertEquals(responseEsperado.email(), responseRecebido.email());
        assertEquals("ADMIN", responseRecebido.perfil());
        assertEquals(senhaCriptografada, usuarioMapeado.getSenha());

        // VERIFY
        verify(usuarioRepository, times(1)).save(usuarioMapeado);
        verify(passwordEncoder).encode(anyString());
    }
}
