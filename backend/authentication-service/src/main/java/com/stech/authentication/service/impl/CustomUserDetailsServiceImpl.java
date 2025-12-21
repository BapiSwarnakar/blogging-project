package com.stech.authentication.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.stech.authentication.entity.UserEntity;
import com.stech.authentication.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;

   @Override
   @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);
        
        UserEntity user = userRepository.findByEmailWithRolesAndPermissions(email)
            .orElseThrow(() -> {
                log.error("User not found with email: {}", email);
                return new UsernameNotFoundException("User not found with email: " + email);
            });
        
        log.debug("User found: {}", user.getName());
        return new CustomUserDetails(user);
    }
}
