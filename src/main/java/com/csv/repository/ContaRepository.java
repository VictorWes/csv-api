package com.csv.repository;

import com.csv.entities.Conta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ContaRepository extends JpaRepository<Conta, UUID> {

    Page<Conta> findAllByAtivoTrue(Pageable paginacao);
    Page<Conta> findAllByAtivoFalse(Pageable paginacao);
    boolean existsByEmpresaId(UUID empresaId);
    Optional<Conta> findByEmpresaId(UUID empresaId);

}
