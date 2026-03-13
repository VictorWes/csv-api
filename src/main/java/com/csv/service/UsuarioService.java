package com.csv.service;

import com.csv.controller.request.UsuarioAtualizacaoRequest;
import com.csv.controller.request.UsuarioRequest;
import com.csv.controller.response.UsuarioResponse;
import com.csv.entities.Usuario;
import com.csv.infra.exception.RecursoNaoEncontradoException;
import com.csv.infra.exception.RegraNegocioException;
import com.csv.mapper.UsuarioMapper;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse criarUsuario(UsuarioRequest request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        var empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada pelo ID informado."));

        var usuario = usuarioMapper.toEntity(request, empresa);
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        var usuarioSalvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuarioSalvo);
    }

    public Page<UsuarioResponse> listarTodos(Pageable paginacao) {
        return usuarioRepository.findAllByAtivoTrue(paginacao)
                .map(usuarioMapper::toResponse);
    }

    public Page<UsuarioResponse> listarInativos(Pageable paginacao) {
        return usuarioRepository.findAllByAtivoFalse(paginacao)
                .map(usuarioMapper::toResponse);
    }

    public UsuarioResponse buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse atualizar(UUID id, UsuarioAtualizacaoRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        if (request.email() != null && !request.email().equals(usuario.getEmail()) && usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        usuario.atualizarInformacoes(request.nome(), request.email(), request.perfil());
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void inativar(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        usuario.inativar();
    }
}
