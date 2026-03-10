package com.csv.repository;

import com.csv.entities.FormaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, UUID> {

    List<FormaPagamento> findByEmpresaId(UUID empresaId);

}
