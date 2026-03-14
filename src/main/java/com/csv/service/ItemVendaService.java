package com.csv.service;

import com.csv.controller.request.ItemVendaAtualizacaoRequest;
import com.csv.controller.request.ItemVendaRequest;
import com.csv.controller.response.ItemVendaResponse;
import com.csv.entities.ItemVenda;
import com.csv.entities.Produto;
import com.csv.entities.Venda;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.ItemVendaMapper;
import com.csv.repository.ItemVendaRepository;
import com.csv.repository.ProdutoRepository;
import com.csv.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ItemVendaService {
    @Autowired private ItemVendaRepository itemVendaRepository;
    @Autowired private VendaRepository vendaRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private ItemVendaMapper itemVendaMapper;

    @Transactional
    public ItemVendaResponse adicionarItem(ItemVendaRequest request) {
        Venda venda = vendaRepository.findById(request.vendaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Venda não encontrada."));

        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado."));

        ItemVenda itemVenda = itemVendaMapper.toEntity(request, venda, produto);
        ItemVenda salvo = itemVendaRepository.save(itemVenda);

        return itemVendaMapper.toResponse(salvo);
    }

    public Page<ItemVendaResponse> listarPorVenda(UUID vendaId, Pageable paginacao) {
        return itemVendaRepository.findAllByVendaIdAndAtivoTrue(vendaId, paginacao)
                .map(itemVendaMapper::toResponse);
    }

    public ItemVendaResponse buscarPorId(UUID id) {
        ItemVenda itemVenda = itemVendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item da venda não encontrado."));
        return itemVendaMapper.toResponse(itemVenda);
    }

    @Transactional
    public ItemVendaResponse atualizarQuantidade(UUID id, ItemVendaAtualizacaoRequest request) {
        ItemVenda itemVenda = itemVendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item da venda não encontrado."));

        itemVenda.atualizarQuantidade(request.quantidade());
        return itemVendaMapper.toResponse(itemVenda);
    }

    @Transactional
    public void removerItem(UUID id) {
        ItemVenda itemVenda = itemVendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item da venda não encontrado."));
        itemVenda.inativar();
    }
}
