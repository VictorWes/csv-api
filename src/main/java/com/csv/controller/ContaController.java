package com.csv.controller;

import com.csv.controller.request.ContaAtualizacaoRequest;
import com.csv.controller.request.ContaRequest;
import com.csv.controller.response.ContaResponse;
import com.csv.service.ContaService;
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
@RequestMapping("/contas")
public class ContaController {
    @Autowired
    private ContaService contaService;

    @PostMapping
    public ResponseEntity<ContaResponse> criar(@RequestBody @Valid ContaRequest request, UriComponentsBuilder uriBuilder) {
        var response = contaService.criarConta(request);
        var uri = uriBuilder.path("/contas/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ContaResponse>> listarTodas(@PageableDefault(size = 10) Pageable paginacao) {
        return ResponseEntity.ok(contaService.listarTodas(paginacao));
    }

    @GetMapping("/inativas")
    public ResponseEntity<Page<ContaResponse>> listarInativas(@PageableDefault(size = 10) Pageable paginacao) {
        return ResponseEntity.ok(contaService.listarInativas(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ContaResponse> atualizarSaldo(@PathVariable UUID id, @RequestBody @Valid ContaAtualizacaoRequest request) {
        return ResponseEntity.ok(contaService.atualizarSaldo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        contaService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
