package com.csv.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.csv.entities.Empresa;
import com.csv.entities.Usuario;
import com.csv.enums.PerfilEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private Usuario usuario;
    private final String secret = "test-secret"; // Segredo de teste

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(tokenService, "secret", secret);

        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());

        usuario = new Usuario();
        usuario.setEmail("usuario@teste.com");
        usuario.setEmpresa(empresa);
        usuario.setPerfil(PerfilEnum.ADMIN);
    }

    @Test
    @DisplayName("Deve gerar um token JWT com sucesso e com as claims corretas")
    void deveGerarTokenJWTComSucesso() {
        // ACT
        String token = tokenService.gerarToken(usuario);

        // ASSERT
        assertNotNull(token);


        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("pdv-api")
                .build()
                .verify(token);

        assertEquals(usuario.getEmail(), decodedJWT.getSubject());
        assertEquals(usuario.getEmpresa().getId().toString(), decodedJWT.getClaim("empresa_id").asString());
        assertEquals(usuario.getPerfil().name(), decodedJWT.getClaim("perfil").asString());
        assertTrue(decodedJWT.getExpiresAtAsInstant().isAfter(Instant.now()));
    }

    @Test
    @DisplayName("Deve retornar o subject (email) de um token JWT válido")
    void deveRetornarSubjectDeTokenValido() {
        // ARRANGE
        String token = tokenService.gerarToken(usuario);

        // ACT
        String subject = tokenService.getSubject(token);

        // ASSERT
        assertEquals(usuario.getEmail(), subject);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException ao tentar gerar token com erro na biblioteca JWT")
    void deveLancarExcecaoAoGerarTokenComSecretInvalido() {
        // ARRANGE
        ReflectionTestUtils.setField(tokenService, "secret", null);

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.gerarToken(usuario);
        });

        assertEquals("Erro ao gerar token JWT", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar string vazia ao tentar validar um token inválido")
    void deveRetornarStringVaziaParaTokenInvalido() {
        // ARRANGE
        String tokenInvalido = "um.token.qualquer";

        // ACT
        String subject = tokenService.getSubject(tokenInvalido);

        // ASSERT
        assertTrue(subject.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar string vazia para um token com assinatura incorreta")
    void deveRetornarStringVaziaParaTokenComAssinaturaIncorreta() {
        // ARRANGE
        String outroSecret = "outro-secret-diferente";
        String tokenComOutraAssinatura = JWT.create()
                .withIssuer("pdv-api")
                .withSubject(usuario.getEmail())
                .sign(Algorithm.HMAC256(outroSecret));

        // ACT
        String subject = tokenService.getSubject(tokenComOutraAssinatura);

        // ASSERT
        assertTrue(subject.isEmpty());
    }
}

