package com.csv.service;

import com.csv.controller.request.ClienteRequest;
import com.csv.controller.response.ClienteResponse;
import com.csv.entities.Cliente;
import com.csv.entities.Empresa;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.ClienteMapper;
import com.csv.repository.ClienteRepository;
import com.csv.repository.EmpresaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ClienteMapper clienteMapper;

    @Transactional
    public ClienteResponse criarCliente(ClienteRequest request) {

        if (clienteRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este e-mail.");
        }

        if (clienteRepository.existsByTelefone(request.telefone())) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este telefone.");
        }

        Empresa empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada pelo ID informado."));
        Cliente cliente = clienteMapper.toEntity(request, empresa);

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return clienteMapper.toResponse(clienteSalvo);
    }

    public Page<ClienteResponse> listarTodos(Pageable paginacao) {
        return clienteRepository.findAll(paginacao)
                .map(clienteMapper::toResponse);
    }

    public ClienteResponse buscarPorId(UUID id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado pelo ID informado."));

        return clienteMapper.toResponse(cliente);
    }
}
