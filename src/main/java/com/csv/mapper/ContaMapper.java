package com.csv.mapper;

import com.csv.controller.request.ContaRequest;
import com.csv.controller.response.ContaResponse;
import com.csv.entities.Conta;
import com.csv.entities.Empresa;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ContaMapper {

    public Conta toEntity(ContaRequest request, Empresa empresa) {
        Conta conta = new Conta();
        conta.setEmpresa(empresa);
        if (request.saldoInicial() != null) {
            conta.setSaldoAtual(request.saldoInicial());
        } else {
            conta.setSaldoAtual(BigDecimal.ZERO);
        }
        return conta;
    }

    public ContaResponse toResponse(Conta conta) {
        return new ContaResponse(
                conta.getId(),
                conta.getSaldoAtual(),
                conta.getEmpresa().getId()
        );
    }
}
