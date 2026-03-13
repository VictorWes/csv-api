package com.csv.controller;

import com.csv.AbstractIntegrationTest;
import com.csv.controller.request.ClienteAtualizacaoRequest;
import com.csv.controller.request.ClienteRequest;
import com.csv.entities.Cliente;
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

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    // --- Helper Method para popular o Testcontainer ---
    private Cliente criarClienteNoBanco(boolean ativo, String email, String telefone) {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        cliente.setEmail(email);
        cliente.setTelefone(telefone);
        cliente.setDataNascimento(dataNascimentoValida);
        cliente.setEmpresa(empresaSalva);
        if (!ativo) {
            cliente.inativar();
        }
        return clienteRepository.save(cliente);
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

    @Test
    @DisplayName("Deve listar apenas clientes ativos (200 OK)")
    @WithMockUser(authorities = "OPERADOR")
    void deveListarClientesAtivos() throws Exception {
        criarClienteNoBanco(true, "ativo@email.com", "11900000001");
        criarClienteNoBanco(false, "inativo@email.com", "11900000002");

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].email").value("ativo@email.com"));
    }

    @Test
    @DisplayName("Deve listar clientes inativos quando usuário for ADMIN (200 OK)")
    @WithMockUser(authorities = "ADMIN")
    void deveListarClientesInativosAdmin() throws Exception {
        criarClienteNoBanco(true, "ativo@email.com", "11900000001");
        criarClienteNoBanco(false, "inativo@email.com", "11900000002");

        mockMvc.perform(get("/clientes/inativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].email").value("inativo@email.com"));
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao tentar listar inativos com perfil OPERADOR")
    @WithMockUser(authorities = "OPERADOR")
    void deveBloquearListagemDeInativosParaOperador() throws Exception {
        mockMvc.perform(get("/clientes/inativos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve atualizar telefone do cliente parcialmente (200 OK)")
    @WithMockUser(authorities = "ADMIN")
    void deveAtualizarClienteComSucesso() throws Exception {
        Cliente cliente = criarClienteNoBanco(true, "atualiza@email.com", "11900000001");
        ClienteAtualizacaoRequest request = new ClienteAtualizacaoRequest(null, null, "11988888888", null);

        mockMvc.perform(patch("/clientes/{id}", cliente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telefone").value("11988888888"))
                .andExpect(jsonPath("$.nome").value(cliente.getNome()));
    }
}
