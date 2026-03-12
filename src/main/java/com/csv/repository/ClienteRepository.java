package com.csv.repository;

import com.csv.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    List<Cliente> findByEmpresaId(UUID empresaId);

    boolean existsByEmail(String email);

    boolean existsByTelefone(String telefone);

    Page<Cliente> findAllByAtivoTrue(Pageable paginacao);

}
