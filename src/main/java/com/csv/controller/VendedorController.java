package com.csv.controller;

import com.csv.controller.request.VendedorAtualizacaoRequest;
import com.csv.controller.request.VendedorRequest;
import com.csv.controller.response.VendedorResponse;
import com.csv.service.VendedorService;
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
@RequestMapping("/vendedores")
public class VendedorController {

    @Autowired
    private VendedorService vendedorService;

    @PostMapping
    public ResponseEntity<VendedorResponse> criar(@RequestBody @Valid VendedorRequest request, UriComponentsBuilder uriBuilder) {
        var response = vendedorService.criar(request);
        var uri = uriBuilder.path("/vendedores/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<Page<VendedorResponse>> listarPorEmpresa(
            @PathVariable UUID empresaId,
            @PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        return ResponseEntity.ok(vendedorService.listarPorEmpresa(empresaId, paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendedorResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(vendedorService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VendedorResponse> atualizar(@PathVariable UUID id, @RequestBody @Valid VendedorAtualizacaoRequest request) {
        return ResponseEntity.ok(vendedorService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        vendedorService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}