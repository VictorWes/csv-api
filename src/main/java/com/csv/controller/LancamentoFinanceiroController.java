package com.csv.controller;

import com.csv.controller.request.LancamentoAtualizacaoRequest;
import com.csv.controller.request.LancamentoFinanceiroRequest;
import com.csv.controller.response.LancamentoFinanceiroResponse;
import com.csv.service.LancamentoFinanceiroService;
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
@RequestMapping("/lancamentos")
public class LancamentoFinanceiroController {

    @Autowired
    private LancamentoFinanceiroService lancamentoService;

    @PostMapping
    public ResponseEntity<LancamentoFinanceiroResponse> criar(@RequestBody @Valid LancamentoFinanceiroRequest request, UriComponentsBuilder uriBuilder) {
        var response = lancamentoService.criar(request);
        var uri = uriBuilder.path("/lancamentos/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/conta/{contaId}")
    public ResponseEntity<Page<LancamentoFinanceiroResponse>> listarPorConta(
            @PathVariable UUID contaId,
            @PageableDefault(size = 20, sort = {"dataCriacao"}) Pageable paginacao) {
        return ResponseEntity.ok(lancamentoService.listarPorConta(contaId, paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LancamentoFinanceiroResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(lancamentoService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LancamentoFinanceiroResponse> atualizarDescricao(
            @PathVariable UUID id,
            @RequestBody @Valid LancamentoAtualizacaoRequest request) {
        return ResponseEntity.ok(lancamentoService.atualizarDescricao(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        lancamentoService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}