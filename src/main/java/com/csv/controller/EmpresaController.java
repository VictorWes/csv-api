package com.csv.controller;

import com.csv.controller.request.EmpresaAtualizacaoRequest;
import com.csv.controller.request.EmpresaRequest;
import com.csv.controller.response.EmpresaResponse;
import com.csv.service.EmpresaService;
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
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @PostMapping
    public ResponseEntity<EmpresaResponse> criar(@RequestBody @Valid EmpresaRequest request, UriComponentsBuilder uriBuilder) {

        var response = empresaService.criarEmpresa(request);
        var uri = uriBuilder.path("/empresas/{id}").buildAndExpand(response.id()).toUri();

        return ResponseEntity.created(uri).body(response);
    }
    @GetMapping
    public ResponseEntity<Page<EmpresaResponse>> listarTodas(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = empresaService.listarTodas(paginacao);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/inativas")
    public ResponseEntity<Page<EmpresaResponse>> listarInativas(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = empresaService.listarInativas(paginacao);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponse> buscarPorId(@PathVariable UUID id) {
        var response = empresaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmpresaResponse> atualizar(@PathVariable UUID id, @RequestBody @Valid EmpresaAtualizacaoRequest request) {
        var response = empresaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        empresaService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
