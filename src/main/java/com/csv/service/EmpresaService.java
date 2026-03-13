package com.csv.service;

import com.csv.controller.request.EmpresaAtualizacaoRequest;
import com.csv.controller.request.EmpresaRequest;
import com.csv.controller.response.EmpresaResponse;
import com.csv.entities.Empresa;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.EmpresaMapper;
import com.csv.repository.EmpresaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private EmpresaMapper empresaMapper;

    @Transactional
    public EmpresaResponse criarEmpresa(EmpresaRequest request) {
        var empresa = empresaMapper.toEntity(request);

        var empresaSalva = empresaRepository.save(empresa);
        return empresaMapper.toResponse(empresaSalva);
    }

    public Page<EmpresaResponse> listarTodas(Pageable paginacao) {
        return empresaRepository.findAllByAtivoTrue(paginacao)
                .map(empresaMapper::toResponse);
    }

    public Page<EmpresaResponse> listarInativas(Pageable paginacao) {
        return empresaRepository.findAllByAtivoFalse(paginacao)
                .map(empresaMapper::toResponse);
    }

    public EmpresaResponse buscarPorId(UUID id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));
        return empresaMapper.toResponse(empresa);
    }

    @Transactional
    public EmpresaResponse atualizar(UUID id, EmpresaAtualizacaoRequest request) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));
        empresa.atualizarInformacoes(request.nome(), request.urlLogo());

        return empresaMapper.toResponse(empresa);
    }

    @Transactional
    public void inativar(UUID id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));
        empresa.inativar();
    }
}
