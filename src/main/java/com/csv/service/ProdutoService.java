package com.csv.service;

import com.csv.controller.request.ProdutoAtualizacaoRequest;
import com.csv.controller.request.ProdutoRequest;
import com.csv.controller.response.ProdutoResponse;
import com.csv.entities.Empresa;
import com.csv.entities.Produto;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.mapper.ProdutoMapper;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ProdutoMapper produtoMapper;

    @Transactional
    public ProdutoResponse criarProduto(ProdutoRequest request) {
        Empresa empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada pelo ID informado."));

        Produto produto = produtoMapper.toEntity(request, empresa);
        Produto produtoSalvo = produtoRepository.save(produto);

        return produtoMapper.toResponse(produtoSalvo);
    }

    public Page<ProdutoResponse> listarTodos(Pageable paginacao) {
        return produtoRepository.findAllByAtivoTrue(paginacao)
                .map(produtoMapper::toResponse);
    }

    public Page<ProdutoResponse> listarInativos(Pageable paginacao) {
        return produtoRepository.findAllByAtivoFalse(paginacao)
                .map(produtoMapper::toResponse);
    }

    public ProdutoResponse buscarPorId(UUID id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado."));
        return produtoMapper.toResponse(produto);
    }

    @Transactional
    public ProdutoResponse atualizar(UUID id, ProdutoAtualizacaoRequest request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado."));

        produto.atualizarInformacoes(
                request.nome(),
                request.preco(),
                request.segmento(),
                request.urlFoto(),
                request.codigoBarras()
        );

        return produtoMapper.toResponse(produto);
    }

    @Transactional
    public void inativar(UUID id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado."));
        produto.inativar();
    }
}
