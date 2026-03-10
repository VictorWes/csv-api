package com.csv.repository;

import com.csv.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProdutorRepository extends JpaRepository<Produto, UUID> {
    List<Produto> findByEmpresaId(UUID empresaId);
}
