package com.csv.service;

import com.csv.entities.Usuario;
import com.csv.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Mock
    private UsuarioRepository usuarioRepository;

    private String emailExistente;
    private String emailNaoExistente;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        emailExistente = "usuario@teste.com";
        emailNaoExistente = "naoexiste@teste.com";
        usuario = new Usuario();
        usuario.setEmail(emailExistente);
    }

    @Test
    @DisplayName("Deve retornar UserDetails quando o usuário for encontrado pelo e-mail")
    void deveRetornarUserDetailsQuandoUsuarioEncontrado() {
        // ARRANGE
        when(usuarioRepository.findByEmail(emailExistente)).thenReturn(Optional.of(usuario));

        // ACT
        UserDetails userDetails = autenticacaoService.loadUserByUsername(emailExistente);

        // ASSERT
        assertNotNull(userDetails);
        assertEquals(emailExistente, userDetails.getUsername());
        verify(usuarioRepository).findByEmail(emailExistente);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando o usuário não for encontrado pelo e-mail")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // ARRANGE
        when(usuarioRepository.findByEmail(emailNaoExistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            autenticacaoService.loadUserByUsername(emailNaoExistente);
        });

        assertEquals("Usuário ou senha inválidos", exception.getMessage());
        verify(usuarioRepository).findByEmail(emailNaoExistente);
    }
}

