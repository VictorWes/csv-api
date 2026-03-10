package com.csv.repository;

import com.csv.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {
}
