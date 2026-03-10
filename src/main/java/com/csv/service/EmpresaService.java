package com.csv.service;

import com.csv.controller.request.EmpresaRequest;
import com.csv.controller.response.EmpresaResponse;
import com.csv.mapper.EmpresaMapper;
import com.csv.repository.EmpresaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
