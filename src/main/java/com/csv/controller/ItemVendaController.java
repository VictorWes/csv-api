package com.csv.controller;

import com.csv.controller.request.ItemVendaAtualizacaoRequest;
import com.csv.controller.request.ItemVendaRequest;
import com.csv.controller.response.ItemVendaResponse;
import com.csv.service.ItemVendaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/itens-venda")
public class ItemVendaController {
    @Autowired
    private ItemVendaService itemVendaService;

    @PostMapping
    public ResponseEntity<ItemVendaResponse> adicionar(@RequestBody @Valid ItemVendaRequest request, UriComponentsBuilder uriBuilder) {
        var response = itemVendaService.adicionarItem(request);
        var uri = uriBuilder.path("/itens-venda/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/venda/{vendaId}")
    public ResponseEntity<Page<ItemVendaResponse>> listarPorVenda(@PathVariable UUID vendaId, @PageableDefault(size = 50) Pageable paginacao) {
        return ResponseEntity.ok(itemVendaService.listarPorVenda(vendaId, paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemVendaResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(itemVendaService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemVendaResponse> atualizarQuantidade(@PathVariable UUID id, @RequestBody @Valid ItemVendaAtualizacaoRequest request) {
        return ResponseEntity.ok(itemVendaService.atualizarQuantidade(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        itemVendaService.removerItem(id);
        return ResponseEntity.noContent().build();
    }
}
