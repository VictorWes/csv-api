package com.csv.repository;

import com.csv.entities.ItemVenda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ItemVendaRepository extends JpaRepository<ItemVenda, UUID> {
    List<ItemVenda> findByVendaId(UUID vendaId);
    Page<ItemVenda> findAllByAtivoTrue(Pageable paginacao);
    Page<ItemVenda> findAllByVendaIdAndAtivoTrue(UUID vendaId, Pageable paginacao);
}

