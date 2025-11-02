package com.api.beard_soft.infra.security;

import com.api.beard_soft.domain.user.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

public class CustomCLientDetails implements UserDetails {
    private final UserEntity user;

    // Método que vamos usar no Controller para pegar o ID
    // NOVO CAMPO: O ID do Cliente (que é o que precisamos para a busca)
    @Getter
    private final Long clientId;

    public CustomCLientDetails(UserEntity user, Long clientId) {
        this.user = user;
        this.clientId =  clientId;
    }

    // --- Implementação obrigatória do UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name()) // Assumindo que Role é um Enum
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // Deixe os outros métodos default (true) ou implemente conforme a necessidade
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
