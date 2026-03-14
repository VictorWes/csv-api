package com.csv.controller;

import com.csv.controller.request.VendaAtualizacaoRequest;
import com.csv.controller.request.VendaRequest;
import com.csv.controller.response.VendaResponse;
import com.csv.service.VendaService;
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
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @PostMapping
    public ResponseEntity<VendaResponse> abrir(@RequestBody @Valid VendaRequest request, UriComponentsBuilder uriBuilder) {
        var response = vendaService.abrirVenda(request);
        var uri = uriBuilder.path("/vendas/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<Page<VendaResponse>> listarPorEmpresa(
            @PathVariable UUID empresaId,
            @PageableDefault(size = 20, sort = {"dataCriacao"}) Pageable paginacao) {
        return ResponseEntity.ok(vendaService.listarPorEmpresa(empresaId, paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VendaResponse> atualizar(@PathVariable UUID id, @RequestBody @Valid VendaAtualizacaoRequest request) {
        return ResponseEntity.ok(vendaService.atualizarVenda(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable UUID id) {
        vendaService.cancelarVenda(id);
        return ResponseEntity.noContent().build();
    }
}