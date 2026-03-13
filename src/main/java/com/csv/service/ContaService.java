package com.csv.service;

import com.csv.controller.request.ContaAtualizacaoRequest;
import com.csv.controller.request.ContaRequest;
import com.csv.controller.response.ContaResponse;
import com.csv.entities.Conta;
import com.csv.entities.Empresa;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.infra.exception.RegraNegocioException;
import com.csv.mapper.ContaMapper;
import com.csv.repository.ContaRepository;
import com.csv.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ContaService {
    @Autowired private ContaRepository contaRepository;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private ContaMapper contaMapper;

    @Transactional
    public ContaResponse criarConta(ContaRequest request) {

        if (contaRepository.existsByEmpresaId(request.empresaId())) {
            throw new RegraNegocioException("Esta empresa já possui uma conta financeira cadastrada.");
        }

        Empresa empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));

        Conta conta = contaMapper.toEntity(request, empresa);
        Conta contaSalva = contaRepository.save(conta);

        return contaMapper.toResponse(contaSalva);
    }

    public Page<ContaResponse> listarTodas(Pageable paginacao) {
        return contaRepository.findAllByAtivoTrue(paginacao)
                .map(contaMapper::toResponse);
    }

    public Page<ContaResponse> listarInativas(Pageable paginacao) {
        return contaRepository.findAllByAtivoFalse(paginacao)
                .map(contaMapper::toResponse);
    }

    public ContaResponse buscarPorId(UUID id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada."));
        return contaMapper.toResponse(conta);
    }

    @Transactional
    public ContaResponse atualizarSaldo(UUID id, ContaAtualizacaoRequest request) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada."));

        conta.ajustarSaldo(request.saldoAtual());
        return contaMapper.toResponse(conta);
    }

    @Transactional
    public void inativar(UUID id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada."));
        conta.inativar();
    }
}
