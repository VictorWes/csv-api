package com.csv.entities;

import com.csv.enums.PerfilEnum;
import com.csv.infra.CryptoConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Data
@Entity
@Table(name = "tb_usuario")
public class Usuario extends BaseEntity implements UserDetails {

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

    @Column(nullable = false)
    private Boolean ativo = true;

    @Override
    public boolean isEnabled() {
        return this.ativo;
    }

    public void inativar() {
        this.ativo = false;
    }

    public void atualizarInformacoes(String nome, String email, PerfilEnum perfil) {
        if (nome != null && !nome.isBlank()) {
            this.nome = nome;
        }
        if (email != null && !email.isBlank()) {
            this.email = email;
        }
        if (perfil != null) {
            this.perfil = perfil;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.perfil.name()));
    }

    @Override
    public @Nullable String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
