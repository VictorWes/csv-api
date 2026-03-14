package com.csv.repository;

import com.csv.entities.LancamentoFinanceiro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LancamentoFinanceiroRepository extends JpaRepository<LancamentoFinanceiro, UUID> {
    Page<LancamentoFinanceiro> findAllByContaIdAndAtivoTrue(UUID contaId, Pageable paginacao);
}
