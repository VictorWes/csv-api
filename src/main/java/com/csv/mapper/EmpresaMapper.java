package com.csv.mapper;

import com.csv.controller.request.EmpresaRequest;
import com.csv.controller.response.EmpresaResponse;
import com.csv.entities.Empresa;
import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper {
    public Empresa toEntity(EmpresaRequest request) {
        Empresa empresa = new Empresa();
        empresa.setNome(request.nome());
        empresa.setUrlLogo(request.urlLogo());
        return empresa;
    }

    public EmpresaResponse toResponse(Empresa empresa) {
        return new EmpresaResponse(
                empresa.getId(),
                empresa.getNome(),
                empresa.getUrlLogo(),
                empresa.getDataCriacao()
        );
    }
}
