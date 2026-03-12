package com.csv.mapper;

import com.csv.controller.request.ClienteRequest;
import com.csv.controller.response.ClienteResponse;
import com.csv.entities.Cliente;
import com.csv.entities.Empresa;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequest request, Empresa empresa) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setTelefone(request.telefone());
        cliente.setDataNascimento(request.dataNascimento());
        cliente.setEmpresa(empresa);
        return cliente;
    }

    public ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getDataNascimento(),
                cliente.getEmpresa().getId()
        );
    }
}
