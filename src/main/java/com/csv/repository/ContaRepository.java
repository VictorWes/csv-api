package com.csv.repository;

import com.csv.entities.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ContaRepository extends JpaRepository<Conta, UUID> {

    Optional<Conta> findByEmpresaId(UUID empresaId);

}
