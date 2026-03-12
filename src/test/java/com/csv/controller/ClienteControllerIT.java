package com.csv.controller;

import com.csv.AbstractIntegrationTest;
import com.csv.controller.request.ClienteRequest;
import com.csv.entities.Empresa;
import com.csv.repository.ClienteRepository;
import com.csv.repository.EmpresaRepository;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
public class ClienteControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ClienteRepository clienteRepository;


    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private Empresa empresaSalva;
    private LocalDate dataNascimentoValida;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
        empresaRepository.deleteAll();

        Empresa empresa = new Empresa();
        empresa.setNome("Loja Matriz CSV");
        empresaSalva = empresaRepository.save(empresa);

        dataNascimentoValida = LocalDate.of(1990, 5, 15);
    }

    @AfterEach
    void tearDown() {
        clienteRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso quando usuário for OPERADOR (201 Created)")
    @WithMockUser(authorities = "OPERADOR")
    void deveCriarClienteComSucessoOperador() throws Exception {
        // ARRANGE
        var request = new ClienteRequest(
                "João Silva",
                "joao@email.com",
                "11999999999",
                dataNascimentoValida,
                empresaSalva.getId()
        );
        String payloadJson = objectMapper.writeValueAsString(request);

        // ACT & ASSERT
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.dataNascimento").value("1990-05-15"))
                .andExpect(jsonPath("$.empresaId").value(empresaSalva.getId().toString()));
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden quando usuário for VENDEDOR (Sem permissão)")
    @WithMockUser(authorities = "VENDEDOR")
    void deveRetornar403QuandoNaoTiverPermissao() throws Exception {
        // ARRANGE
        var request = new ClienteRequest("Invasor", "invasor@email.com", "11999999999", dataNascimentoValida, empresaSalva.getId());
        String payloadJson = objectMapper.writeValueAsString(request);

        // ACT & ASSERT
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando data de nascimento for no futuro")
    @WithMockUser(authorities = "ADMIN")
    void deveRetornar400QuandoDataFutura() throws Exception {
        // ARRANGE
        LocalDate dataFutura = LocalDate.of(2050, 1, 1);
        var request = new ClienteRequest("Viajante do Tempo", "futuro@email.com", "11999999999", dataFutura, empresaSalva.getId());
        String payloadJson = objectMapper.writeValueAsString(request);

        // ACT & ASSERT
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando empresa não existir")
    @WithMockUser(authorities = "ADMIN")
    void deveRetornar404QuandoEmpresaNaoEncontrada() throws Exception {
        // ARRANGE -
        var request = new ClienteRequest("João Silva", "joao@email.com", "11999999999", dataNascimentoValida, UUID.randomUUID());
        String payloadJson = objectMapper.writeValueAsString(request);

        // ACT & ASSERT
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isNotFound());
    }
}
