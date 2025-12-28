package com.stech.authentication.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.stech.authentication.entity.RoleEntity;
import com.stech.authentication.entity.UserEntity;
import com.stech.authentication.enums.Gender;
import com.stech.common.security.util.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String email;
    private final Gender gender;
    private final List<String> roles;
    private final boolean isFullAccess;
    private final boolean isActive;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UserEntity user) {
        this.id = user.getId();
        this.username = user.getName();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.roles = getRoles(user);
        this.isFullAccess = getIsFullAccess(user);
        this.authorities = getAuthorities(user);
        this.isActive = user.isActive();
    }

    private List<String> getRoles(UserEntity user) {
        return user.getRoles().stream().map(role -> SecurityUtils.ROLE_PREFIX + role.getName().toUpperCase()).toList();
    }

    private boolean getIsFullAccess(UserEntity user) {
        return user.getRoles().stream().anyMatch(RoleEntity::isFullAccess);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserEntity user) {
        Set<GrantedAuthority> authoritiesSet = new HashSet<>();
        boolean isFullAccessCheck = getIsFullAccess(user);
        log.info("User {} has full access: {}", user.getName(), isFullAccessCheck);
        if (isFullAccessCheck) {
            user.getRoles().forEach(role -> {
                authoritiesSet.add(new SimpleGrantedAuthority(SecurityUtils.ROLE_PREFIX + role.getName().toUpperCase()));
            });
            authoritiesSet.add(new SimpleGrantedAuthority("FULL_ACCESS"));
        }else {
            // Add roles
            user.getRoles().forEach(role -> {
                authoritiesSet.add(new SimpleGrantedAuthority(SecurityUtils.ROLE_PREFIX + role.getName().toUpperCase()));
                // Add permissions from roles
                role.getPermissions().forEach(permission -> 
                    authoritiesSet.add(new SimpleGrantedAuthority(permission.getSlug())));
            });
            // Add direct permissions
            user.getDirectPermissions().forEach(permission -> 
                authoritiesSet.add(new SimpleGrantedAuthority(permission.getSlug())));
        }
        return Collections.unmodifiableSet(authoritiesSet);
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public Gender getGender() { return gender; }
    public List<String> getRoles() { return roles; }
    public boolean getIsFullAccess() { return isFullAccess; }
    
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return isActive; }

}