package com.csv.repository;

import com.csv.entities.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface VendaRepository extends JpaRepository<Venda, UUID>{
    List<Venda> findByEmpresaId(UUID empresaId);
}
