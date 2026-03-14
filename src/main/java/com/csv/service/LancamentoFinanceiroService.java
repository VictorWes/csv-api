package com.csv.service;

import com.csv.controller.request.LancamentoAtualizacaoRequest;
import com.csv.controller.request.LancamentoFinanceiroRequest;
import com.csv.controller.response.LancamentoFinanceiroResponse;
import com.csv.entities.Conta;
import com.csv.entities.LancamentoFinanceiro;
import com.csv.entities.Venda;
import com.csv.enums.TipoOperacaoEnum;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.LancamentoFinanceiroMapper;
import com.csv.repository.ContaRepository;
import com.csv.repository.LancamentoFinanceiroRepository;
import com.csv.repository.VendaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LancamentoFinanceiroService {

    @Autowired private LancamentoFinanceiroRepository lancamentoRepository;
    @Autowired private ContaRepository contaRepository;
    @Autowired private VendaRepository vendaRepository;
    @Autowired private LancamentoFinanceiroMapper lancamentoMapper;

    @Transactional
    public LancamentoFinanceiroResponse criar(LancamentoFinanceiroRequest request) {
        Conta conta = contaRepository.findById(request.contaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada."));
        Venda venda = null;
        if (request.vendaId() != null) {
            venda = vendaRepository.findById(request.vendaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Venda não encontrada."));
        }
        if (request.tipoOperacao() == TipoOperacaoEnum.ENTRADA) {
            conta.creditar(request.valor());
        } else if (request.tipoOperacao() == TipoOperacaoEnum.SAIDA) {
            conta.debitar(request.valor());
        }

        LancamentoFinanceiro lancamento = lancamentoMapper.toEntity(request, conta, venda);
        LancamentoFinanceiro salvo = lancamentoRepository.save(lancamento);

        return lancamentoMapper.toResponse(salvo);
    }

    public Page<LancamentoFinanceiroResponse> listarPorConta(UUID contaId, Pageable paginacao) {
        return lancamentoRepository.findAllByContaIdAndAtivoTrue(contaId, paginacao)
                .map(lancamentoMapper::toResponse);
    }

    public LancamentoFinanceiroResponse buscarPorId(UUID id) {
        LancamentoFinanceiro lancamento = lancamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lançamento não encontrado."));
        return lancamentoMapper.toResponse(lancamento);
    }

    @Transactional
    public LancamentoFinanceiroResponse atualizarDescricao(UUID id, LancamentoAtualizacaoRequest request) {
        LancamentoFinanceiro lancamento = lancamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lançamento não encontrado."));

        lancamento.atualizarDescricao(request.descricao());
        return lancamentoMapper.toResponse(lancamento);
    }

    @Transactional
    public void inativar(UUID id) {
        LancamentoFinanceiro lancamento = lancamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lançamento não encontrado."));
        if (lancamento.getTipoOperacao() == TipoOperacaoEnum.ENTRADA) {
            lancamento.getConta().debitar(lancamento.getValor());
        } else if (lancamento.getTipoOperacao() == TipoOperacaoEnum.SAIDA) {
            lancamento.getConta().creditar(lancamento.getValor());
        }

        lancamento.inativar();
    }
}