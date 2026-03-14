package com.csv.repository;

import com.csv.entities.FormaPagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, UUID> {

    List<FormaPagamento> findByEmpresaId(UUID empresaId);
    Page<FormaPagamento> findAllByAtivoTrue(Pageable paginacao);
    Page<FormaPagamento> findAllByAtivoFalse(Pageable paginacao);

}
