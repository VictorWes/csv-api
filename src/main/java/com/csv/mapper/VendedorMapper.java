package com.csv.mapper;

import com.csv.controller.request.VendedorRequest;
import com.csv.controller.response.VendedorResponse;
import com.csv.entities.Empresa;
import com.csv.entities.Vendedor;
import org.springframework.stereotype.Component;

@Component
public class VendedorMapper {

    public Vendedor toEntity(VendedorRequest request, Empresa empresa) {
        Vendedor vendedor = new Vendedor();
        vendedor.setNome(request.nome());
        vendedor.setCargo(request.cargo());
        vendedor.setDataNascimento(request.dataNascimento());
        vendedor.setEmpresa(empresa);
        return vendedor;
    }

    public VendedorResponse toResponse(Vendedor vendedor) {
        return new VendedorResponse(
                vendedor.getId(),
                vendedor.getNome(),
                vendedor.getCargo(),
                vendedor.getDataNascimento(),
                vendedor.getEmpresa().getId(),
                vendedor.getAtivo()
        );
    }
}