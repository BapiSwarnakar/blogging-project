package com.stech.authentication.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.stech.authentication.entity.UserEntity;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UserEntity user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.authorities = getAuthorities(user);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserEntity user) {
        Set<GrantedAuthority> authoritiesSet = new HashSet<>();
        
        // Add roles
        user.getRoles().forEach(role -> {
            authoritiesSet.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
            // Add permissions from roles
            role.getPermissions().forEach(permission -> 
                authoritiesSet.add(new SimpleGrantedAuthority(permission.getSlug())));
        });
        
        // Add direct permissions
        user.getDirectPermissions().forEach(permission -> 
            authoritiesSet.add(new SimpleGrantedAuthority(permission.getSlug())));
        
        return Collections.unmodifiableSet(authoritiesSet);
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}