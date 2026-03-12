package com.csv.entities;

import com.csv.infra.CryptoConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "tb_cliente")
@Data
public class Cliente extends BaseEntity{

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    @Column(unique = true)
    @Convert(converter = CryptoConverter.class)
    private String email;

    @NotBlank(message = "O telefone é obrigatório")
    @Column(unique = true)
    @Convert(converter = CryptoConverter.class)
    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}
