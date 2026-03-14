package com.csv.service;

import com.csv.controller.request.FormaPagamentoAtualizacaoRequest;
import com.csv.controller.request.FormaPagamentoRequest;
import com.csv.controller.response.FormaPagamentoResponse;
import com.csv.entities.Empresa;
import com.csv.entities.FormaPagamento;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.FormaPagamentoMapper;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.FormaPagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FormaPagamentoService {

    @Autowired private FormaPagamentoRepository formaPagamentoRepository;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private FormaPagamentoMapper formaPagamentoMapper;

    @Transactional
    public FormaPagamentoResponse criarFormaPagamento(FormaPagamentoRequest request) {
        Empresa empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));

        FormaPagamento formaPagamento = formaPagamentoMapper.toEntity(request, empresa);
        FormaPagamento salva = formaPagamentoRepository.save(formaPagamento);

        return formaPagamentoMapper.toResponse(salva);
    }

    public Page<FormaPagamentoResponse> listarTodas(Pageable paginacao) {
        return formaPagamentoRepository.findAllByAtivoTrue(paginacao)
                .map(formaPagamentoMapper::toResponse);
    }

    public Page<FormaPagamentoResponse> listarInativas(Pageable paginacao) {
        return formaPagamentoRepository.findAllByAtivoFalse(paginacao)
                .map(formaPagamentoMapper::toResponse);
    }

    public FormaPagamentoResponse buscarPorId(UUID id) {
        FormaPagamento formaPagamento = formaPagamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Forma de pagamento não encontrada."));
        return formaPagamentoMapper.toResponse(formaPagamento);
    }

    @Transactional
    public FormaPagamentoResponse atualizar(UUID id, FormaPagamentoAtualizacaoRequest request) {
        FormaPagamento formaPagamento = formaPagamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Forma de pagamento não encontrada."));

        formaPagamento.atualizarInformacoes(request.nome(), request.tipoBase());
        return formaPagamentoMapper.toResponse(formaPagamento);
    }

    @Transactional
    public void inativar(UUID id) {
        FormaPagamento formaPagamento = formaPagamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Forma de pagamento não encontrada."));
        formaPagamento.inativar();
    }
}
