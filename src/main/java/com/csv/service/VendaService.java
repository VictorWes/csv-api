package com.csv.service;

import com.csv.controller.request.VendaAtualizacaoRequest;
import com.csv.controller.request.VendaRequest;
import com.csv.controller.response.VendaResponse;
import com.csv.entities.*;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.VendaMapper;
import com.csv.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VendaService {

    @Autowired private VendaRepository vendaRepository;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private FormaPagamentoRepository formaPagamentoRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private VendedorRepository vendedorRepository; // Assumindo que você tem esse
    @Autowired private VendaMapper vendaMapper;

    @Transactional
    public VendaResponse abrirVenda(VendaRequest request) {
        Empresa empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));

        FormaPagamento forma = formaPagamentoRepository.findById(request.formaPagamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Forma de pagamento não encontrada."));

        Cliente cliente = request.clienteId() != null ?
                clienteRepository.findById(request.clienteId()).orElse(null) : null;

        Vendedor vendedor = request.vendedorId() != null ?
                vendedorRepository.findById(request.vendedorId()).orElse(null) : null;

        Venda venda = vendaMapper.toEntity(request, empresa, forma, cliente, vendedor);
        Venda salva = vendaRepository.save(venda);

        return vendaMapper.toResponse(salva);
    }

    public Page<VendaResponse> listarPorEmpresa(UUID empresaId, Pageable paginacao) {
        return vendaRepository.findAllByEmpresaIdAndAtivoTrue(empresaId, paginacao)
                .map(vendaMapper::toResponse);
    }

    public VendaResponse buscarPorId(UUID id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Venda não encontrada."));
        // Recalcula dinamicamente para garantir que o response sempre traga o total real dos itens!
        venda.recalcularValorTotal();
        return vendaMapper.toResponse(venda);
    }

    @Transactional
    public VendaResponse atualizarVenda(UUID id, VendaAtualizacaoRequest request) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Venda não encontrada."));

        FormaPagamento forma = request.formaPagamentoId() != null ?
                formaPagamentoRepository.findById(request.formaPagamentoId()).orElse(null) : venda.getFormaPagamento();

        Cliente cliente = request.clienteId() != null ?
                clienteRepository.findById(request.clienteId()).orElse(null) : venda.getCliente();

        Vendedor vendedor = request.vendedorId() != null ?
                vendedorRepository.findById(request.vendedorId()).orElse(null) : venda.getVendedor();

        venda.atualizarDados(cliente, vendedor, forma);
        return vendaMapper.toResponse(venda);
    }

    @Transactional
    public void cancelarVenda(UUID id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Venda não encontrada."));
        venda.inativar();
    }
}