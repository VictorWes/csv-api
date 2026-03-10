package com.csv.repository;


import com.csv.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface VendedorRepository extends JpaRepository<Cliente, UUID> {

    List<Cliente> findByEmpresaId(UUID empresaId);
}
