package com.csv.service;

import com.csv.controller.request.UsuarioRequest;
import com.csv.controller.response.UsuarioResponse;
import com.csv.mapper.UsuarioMapper;
import com.csv.repository.EmpresaRepository;
import com.csv.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
            throw new RuntimeException("Já existe um usuário cadastrado com este e-mail.");
        }

        var empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada pelo ID informado."));

        var usuario = usuarioMapper.toEntity(request, empresa);
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        var usuarioSalvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuarioSalvo);
    }
}
