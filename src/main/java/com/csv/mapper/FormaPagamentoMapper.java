package com.csv.mapper;

import com.csv.controller.request.FormaPagamentoRequest;
import com.csv.controller.response.FormaPagamentoResponse;
import com.csv.entities.Empresa;
import com.csv.entities.FormaPagamento;
import org.springframework.stereotype.Component;

@Component
public class FormaPagamentoMapper {
    public FormaPagamento toEntity(FormaPagamentoRequest request, Empresa empresa) {
        FormaPagamento formaPagamento = new FormaPagamento();
        formaPagamento.setNome(request.nome());
        formaPagamento.setTipoBase(request.tipoBase());
        formaPagamento.setEmpresa(empresa);
        return formaPagamento;
    }

    public FormaPagamentoResponse toResponse(FormaPagamento formaPagamento) {
        return new FormaPagamentoResponse(
                formaPagamento.getId(),
                formaPagamento.getNome(),
                formaPagamento.getTipoBase(),
                formaPagamento.getEmpresa().getId()
        );
    }
}
