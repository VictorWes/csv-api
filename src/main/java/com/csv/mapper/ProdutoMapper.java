package com.csv.mapper;

import com.csv.controller.request.ProdutoRequest;
import com.csv.controller.response.ProdutoResponse;
import com.csv.entities.Empresa;
import com.csv.entities.Produto;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    public Produto toEntity(ProdutoRequest request, Empresa empresa) {
        Produto produto = new Produto();
        produto.setNome(request.nome());
        produto.setPreco(request.preco());
        produto.setSegmento(request.segmento());
        produto.setUrlFoto(request.urlFoto());
        produto.setCodigoBarras(request.codigoBarras());
        produto.setEmpresa(empresa);

        return produto;
    }

    public ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getPreco(),
                produto.getSegmento(),
                produto.getUrlFoto(),
                produto.getCodigoBarras(),
                produto.getEmpresa().getId()
        );
    }
}
