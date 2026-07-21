package com.example.demo.auth;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.auth.entity.User;

public class CustomUserDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long userId;
    private final String email;
    private final String password;
    private final String role;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }

    public CustomUserDetails(
            Long userId,
            String email,
            String password,
            String role
    ) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = normalizeRole(role);
        this.authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + this.role)
        );
    }

    private static String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("사용자 역할이 필요합니다.");
        }

        String normalized = role.trim().toUpperCase(Locale.ROOT);
        return normalized.startsWith("ROLE_")
                ? normalized.substring("ROLE_".length())
                : normalized;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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

    @Override
    public boolean isEnabled() {
        return true;
    }
}
