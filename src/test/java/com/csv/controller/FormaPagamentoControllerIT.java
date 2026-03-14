package com.csv.controller;

import com.csv.AbstractIntegrationTest;
import com.csv.controller.request.FormaPagamentoAtualizacaoRequest;
import com.csv.controller.request.FormaPagamentoRequest;
import com.csv.entities.Empresa;
import com.csv.entities.FormaPagamento;
import com.csv.enums.TipoBasePagamentoEnum;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.FormaPagamentoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class FormaPagamentoControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FormaPagamentoRepository formaPagamentoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Empresa empresaSalva;

    @BeforeEach
    void setUp() {
        formaPagamentoRepository.deleteAll();
        empresaRepository.deleteAll();

        Empresa empresa = new Empresa();
        empresa.setNome("Loja CSV");
        empresaSalva = empresaRepository.save(empresa);
    }

    @AfterEach
    void tearDown() {
        formaPagamentoRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    private FormaPagamento criarFormaPagamentoNoBanco(boolean ativo, String nome, TipoBasePagamentoEnum tipo) {
        FormaPagamento formaPagamento = new FormaPagamento();
        formaPagamento.setNome(nome);
        formaPagamento.setTipoBase(tipo);
        formaPagamento.setEmpresa(empresaSalva);
        if (!ativo) {
            formaPagamento.inativar();
        }
        return formaPagamentoRepository.save(formaPagamento);
    }


    @Test
    @DisplayName("Deve criar forma de pagamento e retornar 201 Created (GERENTE/ADMIN)")
    @WithMockUser(authorities = "GERENTE")
    void deveCriarFormaPagamentoComSucesso() throws Exception {
        var request = new FormaPagamentoRequest("Dinheiro", TipoBasePagamentoEnum.DINHEIRO, empresaSalva.getId());

        mockMvc.perform(post("/formas-pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Dinheiro"));
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao tentar criar forma de pagamento sendo OPERADOR")
    @WithMockUser(authorities = "OPERADOR")
    void deveBloquearCriacaoParaOperador() throws Exception {
        var request = new FormaPagamentoRequest("Pix", TipoBasePagamentoEnum.PIX, empresaSalva.getId());

        mockMvc.perform(post("/formas-pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }



    @Test
    @DisplayName("Deve listar apenas formas ativas com sucesso (Qualquer perfil logado)")
    @WithMockUser(authorities = "OPERADOR") // Operadores podem listar
    void deveListarAtivas() throws Exception {
        criarFormaPagamentoNoBanco(true, "Pix Ativo", TipoBasePagamentoEnum.PIX);
        criarFormaPagamentoNoBanco(false, "Cartão Inativo", TipoBasePagamentoEnum.PIX);

        mockMvc.perform(get("/formas-pagamento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Pix Ativo"));
    }


    @Test
    @DisplayName("Deve atualizar a forma de pagamento (200 OK) quando ADMIN")
    @WithMockUser(authorities = "ADMIN")
    void deveAtualizarFormaPagamento() throws Exception {
        FormaPagamento forma = criarFormaPagamentoNoBanco(true, "Débito", TipoBasePagamentoEnum.PIX);
        FormaPagamentoAtualizacaoRequest request = new FormaPagamentoAtualizacaoRequest("Cartão Débito Master", null);

        mockMvc.perform(patch("/formas-pagamento/{id}", forma.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cartão Débito Master"));
    }

    @Test
    @DisplayName("Deve inativar forma de pagamento com sucesso (204 No Content) quando GERENTE")
    @WithMockUser(authorities = "GERENTE")
    void deveInativarFormaPagamento() throws Exception {
        FormaPagamento forma = criarFormaPagamentoNoBanco(true, "Cheque", TipoBasePagamentoEnum.OUTROS);

        mockMvc.perform(delete("/formas-pagamento/{id}", forma.getId()))
                .andExpect(status().isNoContent());

        FormaPagamento noBanco = formaPagamentoRepository.findById(forma.getId()).orElseThrow();
        assertFalse(noBanco.getAtivo());
    }
}
