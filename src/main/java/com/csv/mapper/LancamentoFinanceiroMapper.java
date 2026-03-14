package com.csv.mapper;

import com.csv.controller.request.LancamentoFinanceiroRequest;
import com.csv.controller.response.LancamentoFinanceiroResponse;
import com.csv.entities.Conta;
import com.csv.entities.LancamentoFinanceiro;
import com.csv.entities.Venda;
import org.springframework.stereotype.Component;

@Component
public class LancamentoFinanceiroMapper {

    public LancamentoFinanceiro toEntity(LancamentoFinanceiroRequest request, Conta conta, Venda venda) {
        LancamentoFinanceiro lancamento = new LancamentoFinanceiro();
        lancamento.setConta(conta);
        lancamento.setTipoOperacao(request.tipoOperacao());
        lancamento.setValor(request.valor());
        lancamento.setDescricao(request.descricao());
        lancamento.setVenda(venda); // Pode ser nulo
        return lancamento;
    }

    public LancamentoFinanceiroResponse toResponse(LancamentoFinanceiro lancamento) {
        return new LancamentoFinanceiroResponse(
                lancamento.getId(),
                lancamento.getConta().getId(),
                lancamento.getTipoOperacao(),
                lancamento.getValor(),
                lancamento.getDescricao(),
                lancamento.getVenda() != null ? lancamento.getVenda().getId() : null
        );
    }
}
