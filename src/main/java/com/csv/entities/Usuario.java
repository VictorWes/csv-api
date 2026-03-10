package com.csv.entities;

import com.csv.enums.PerfilEnum;
import com.csv.infra.CryptoConverter;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_usuario")
public class Usuario extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nome;

    @Convert(converter = CryptoConverter.class)
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PerfilEnum perfil;
}
