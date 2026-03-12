package com.csv.controller;

import com.csv.controller.request.ClienteAtualizacaoRequest;
import com.csv.controller.request.ClienteRequest;
import com.csv.controller.response.ClienteResponse;
import com.csv.service.ClienteService;
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
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@RequestBody @Valid ClienteRequest request, UriComponentsBuilder uriBuilder) {

        var response = clienteService.criarCliente(request);

        var uri = uriBuilder.path("/clientes/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = clienteService.listarTodos(paginacao);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> detalhar(@PathVariable UUID id) {
        var response = clienteService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable UUID id, @RequestBody ClienteAtualizacaoRequest request) {
        var response = clienteService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        clienteService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/inativos")
    public ResponseEntity<Page<ClienteResponse>> listarInativos(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = clienteService.listarInativos(paginacao);
        return ResponseEntity.ok(page);
    }
}
