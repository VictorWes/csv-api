package com.csv.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "tb_vendedor")
@Data
public class Vendedor extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 50)
    private String cargo;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}
