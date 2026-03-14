package com.csv.controller;

import com.csv.controller.request.FormaPagamentoAtualizacaoRequest;
import com.csv.controller.request.FormaPagamentoRequest;
import com.csv.controller.response.FormaPagamentoResponse;
import com.csv.service.FormaPagamentoService;
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
@RequestMapping("/formas-pagamento")
public class FormaPagamentoController {

    @Autowired
    private FormaPagamentoService formaPagamentoService;

    @PostMapping
    public ResponseEntity<FormaPagamentoResponse> criar(@RequestBody @Valid FormaPagamentoRequest request, UriComponentsBuilder uriBuilder) {
        var response = formaPagamentoService.criarFormaPagamento(request);
        var uri = uriBuilder.path("/formas-pagamento/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<FormaPagamentoResponse>> listarTodas(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        return ResponseEntity.ok(formaPagamentoService.listarTodas(paginacao));
    }

    @GetMapping("/inativas")
    public ResponseEntity<Page<FormaPagamentoResponse>> listarInativas(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        return ResponseEntity.ok(formaPagamentoService.listarInativas(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormaPagamentoResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(formaPagamentoService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FormaPagamentoResponse> atualizar(@PathVariable UUID id, @RequestBody @Valid FormaPagamentoAtualizacaoRequest request) {
        return ResponseEntity.ok(formaPagamentoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        formaPagamentoService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
