package com.csv.mapper;

import com.csv.controller.request.LancamentoFinanceiroRequest;
import com.csv.controller.response.LancamentoFinanceiroResponse;
import com.csv.entities.Conta;
import com.csv.entities.LancamentoFinanceiro;
import com.csv.entities.Venda;
import com.csv.enums.TipoOperacaoEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LancamentoFinanceiroMapperTest {

    private LancamentoFinanceiroMapper mapper;
    private Conta conta;
    private Venda venda;
    private UUID contaId;
    private UUID vendaId;

    @BeforeEach
    void setUp() {
        mapper = new LancamentoFinanceiroMapper();

        contaId = UUID.randomUUID();
        conta = new Conta();
        conta.setId(contaId);

        vendaId = UUID.randomUUID();
        venda = new Venda();
        venda.setId(vendaId);
    }

    @Test
    @DisplayName("Deve converter Request para Entidade com Venda preenchida")
    void deveConverterRequestParaEntityComVenda() {
        LancamentoFinanceiroRequest request = new LancamentoFinanceiroRequest(
                contaId, TipoOperacaoEnum.ENTRADA, new BigDecimal("100.00"), "Venda PDV", vendaId
        );

        LancamentoFinanceiro entity = mapper.toEntity(request, conta, venda);

        assertNotNull(entity);
        assertEquals(conta, entity.getConta());
        assertEquals(venda, entity.getVenda());
        assertEquals(TipoOperacaoEnum.ENTRADA, entity.getTipoOperacao());
        assertEquals(new BigDecimal("100.00"), entity.getValor());
    }

    @Test
    @DisplayName("Deve converter Entidade para Response tratando Venda nula")
    void deveConverterEntityParaResponseComVendaNula() {
        LancamentoFinanceiro entity = new LancamentoFinanceiro();
        entity.setId(UUID.randomUUID());
        entity.setConta(conta);
        entity.setVenda(null);
        entity.setTipoOperacao(TipoOperacaoEnum.SAIDA);
        entity.setValor(new BigDecimal("50.00"));
        entity.setDescricao("Pagamento de Luz");

        LancamentoFinanceiroResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(contaId, response.contaId());
        assertNull(response.vendaId());
        assertEquals("Pagamento de Luz", response.descricao());
    }
}