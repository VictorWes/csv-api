package com.csv.mapper;

import com.csv.controller.request.VendaRequest;
import com.csv.controller.response.VendaResponse;
import com.csv.entities.*;
import org.springframework.stereotype.Component;

@Component
public class VendaMapper {

    public Venda toEntity(VendaRequest request, Empresa empresa, FormaPagamento formaPagamento, Cliente cliente, Vendedor vendedor) {
        Venda venda = new Venda();
        venda.setEmpresa(empresa);
        venda.setFormaPagamento(formaPagamento);
        venda.setCliente(cliente);
        venda.setVendedor(vendedor);
        return venda;
    }

    public VendaResponse toResponse(Venda venda) {
        return new VendaResponse(
                venda.getId(),
                venda.getValorTotal(),
                venda.getEmpresa().getId(),
                venda.getFormaPagamento().getId(),
                venda.getCliente() != null ? venda.getCliente().getId() : null,
                venda.getVendedor() != null ? venda.getVendedor().getId() : null,
                venda.getAtivo()
        );
    }
}