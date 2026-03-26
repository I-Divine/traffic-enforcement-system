package com.project.traffic_enforcement.security.config;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.project.traffic_enforcement.models.Users;

import lombok.Getter;

public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {

    @Getter
    private UUID userId;
    private String email;
    private String password;
    private List<GrantedAuthority> authorities;

    public UserDetails(UUID userId, String email, String password, List<GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetails build(Users user){
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toString());

        return new UserDetails(
                user.getUserId(),
                user.getEmail(),
                user.getPassword(),
                List.of(authority)
        );
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
