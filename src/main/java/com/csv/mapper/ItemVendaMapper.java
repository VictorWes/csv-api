package com.csv.mapper;

import com.csv.controller.request.ItemVendaRequest;
import com.csv.controller.response.ItemVendaResponse;
import com.csv.entities.ItemVenda;
import com.csv.entities.Produto;
import com.csv.entities.Venda;
import org.springframework.stereotype.Component;

@Component
public class ItemVendaMapper {
    public ItemVenda toEntity(ItemVendaRequest request, Venda venda, Produto produto) {
        ItemVenda item = new ItemVenda();
        item.setQuantidade(request.quantidade());
        item.setPrecoUnitario(request.precoUnitario());
        item.setVenda(venda);
        item.setProduto(produto);
        return item;
    }

    public ItemVendaResponse toResponse(ItemVenda itemVenda) {
        return new ItemVendaResponse(
                itemVenda.getId(),
                itemVenda.getQuantidade(),
                itemVenda.getPrecoUnitario(),
                itemVenda.getSubtotal(),
                itemVenda.getVenda().getId(),
                itemVenda.getProduto().getId(),
                itemVenda.getProduto().getNome()
        );
    }
}
