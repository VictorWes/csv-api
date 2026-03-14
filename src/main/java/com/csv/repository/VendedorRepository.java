package com.csv.repository;


import com.csv.entities.Cliente;
import com.csv.entities.Vendedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface VendedorRepository extends JpaRepository<Vendedor, UUID> {
    List<Cliente> findByEmpresaId(UUID empresaId);
    Page<Vendedor> findAllByEmpresaIdAndAtivoTrue(UUID empresaId, Pageable paginacao);
}
