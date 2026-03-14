package com.csv.service;

import com.csv.controller.request.VendedorAtualizacaoRequest;
import com.csv.controller.request.VendedorRequest;
import com.csv.controller.response.VendedorResponse;
import com.csv.entities.Empresa;
import com.csv.entities.Vendedor;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.VendedorMapper;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.VendedorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VendedorService {

    @Autowired private VendedorRepository vendedorRepository;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private VendedorMapper vendedorMapper;

    @Transactional
    public VendedorResponse criar(VendedorRequest request) {
        Empresa empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));

        Vendedor vendedor = vendedorMapper.toEntity(request, empresa);
        Vendedor salvo = vendedorRepository.save(vendedor);

        return vendedorMapper.toResponse(salvo);
    }

    public Page<VendedorResponse> listarPorEmpresa(UUID empresaId, Pageable paginacao) {
        return vendedorRepository.findAllByEmpresaIdAndAtivoTrue(empresaId, paginacao)
                .map(vendedorMapper::toResponse);
    }

    public VendedorResponse buscarPorId(UUID id) {
        Vendedor vendedor = vendedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vendedor não encontrado."));
        return vendedorMapper.toResponse(vendedor);
    }

    @Transactional
    public VendedorResponse atualizar(UUID id, VendedorAtualizacaoRequest request) {
        Vendedor vendedor = vendedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vendedor não encontrado."));

        vendedor.atualizarDados(request.nome(), request.cargo(), request.dataNascimento());
        return vendedorMapper.toResponse(vendedor);
    }

    @Transactional
    public void inativar(UUID id) {
        Vendedor vendedor = vendedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vendedor não encontrado."));
        vendedor.inativar();
    }
}