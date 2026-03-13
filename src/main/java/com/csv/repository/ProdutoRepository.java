package com.csv.repository;

import com.csv.entities.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    List<Produto> findByEmpresaId(UUID empresaId);

    Page<Produto> findAllByAtivoTrue(Pageable paginacao);
    Page<Produto> findAllByAtivoFalse(Pageable paginacao);
}
