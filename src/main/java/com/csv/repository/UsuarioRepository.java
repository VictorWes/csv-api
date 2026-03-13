package com.csv.repository;

import com.csv.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByEmpresaId(UUID empresaId);

    Page<Usuario> findAllByAtivoTrue(Pageable paginacao);
    Page<Usuario> findAllByAtivoFalse(Pageable paginacao);
}
