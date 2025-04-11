package com.stech.authentication.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.stech.authentication.entity.PermissionEntity;
import com.stech.authentication.entity.RoleEntity;
import com.stech.authentication.entity.UserEntity;
import com.stech.authentication.repository.PermissionRepository;
import com.stech.authentication.repository.RoleRepository;
import com.stech.authentication.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (shouldSeedData()) {
            seedPermissions();
            seedRoles();
            seedAdminUser();
            seedTestUsers();
            log.info("Database seeding completed successfully");
        }
    }

    private boolean shouldSeedData() {
        return userRepository.count() == 0 && 
               roleRepository.count() == 0 && 
               permissionRepository.count() == 0;
    }

    private void seedPermissions() {
        List<PermissionEntity> permissions = Arrays.asList(
            createPermission("USER_READ", "View user information"),
            createPermission("USER_CREATE", "Create new users"),
            createPermission("USER_UPDATE", "Update user information"),
            createPermission("USER_DELETE", "Delete users"),
            
            createPermission("ROLE_MANAGE", "Manage roles and permissions"),
            
            createPermission("PRODUCT_READ", "View products"),
            createPermission("PRODUCT_CREATE", "Create products"),
            createPermission("PRODUCT_UPDATE", "Update products"),
            createPermission("PRODUCT_DELETE", "Delete products"),
            
            createPermission("ORDER_MANAGE", "Manage orders"),
            createPermission("DASHBOARD_VIEW", "View dashboard"),
            
            createPermission("SETTINGS_MANAGE", "Manage system settings"),
            createPermission("AUDIT_VIEW", "View audit logs")
        );

        permissionRepository.saveAll(permissions);
        log.info("Seeded {} permissions", permissions.size());
    }

    private void seedRoles() {
        // Get all permissions
        List<PermissionEntity> allPermissions = permissionRepository.findAll();
        
        // Admin role - all permissions
        RoleEntity adminRole = RoleEntity.builder()
            .name("ADMIN")
            .description("System Administrator with full access")
            .permissions(new HashSet<>(allPermissions))
            .build();
        
        // Manager role - limited permissions
        Set<PermissionEntity> managerPermissions = allPermissions.stream()
            .filter(p -> !p.getName().endsWith("_DELETE") && 
                       !p.getName().equals("ROLE_MANAGE"))
            .collect(Collectors.toSet());
        
        RoleEntity managerRole = RoleEntity.builder()
            .name("MANAGER")
            .description("Department Manager with limited administrative access")
            .permissions(managerPermissions)
            .build();
        
        // User role - basic permissions
        Set<PermissionEntity> userPermissions = allPermissions.stream()
            .filter(p -> p.getName().equals("USER_READ") || 
                        p.getName().equals("PRODUCT_READ") || 
                        p.getName().equals("DASHBOARD_VIEW"))
            .collect(Collectors.toSet());
        
        RoleEntity userRole = RoleEntity.builder()
            .name("USER")
            .description("Regular system user")
            .permissions(userPermissions)
            .build();
        
        roleRepository.saveAll(List.of(adminRole, managerRole, userRole));
        log.info("Seeded 3 roles: ADMIN, MANAGER, USER");
    }

    private void seedAdminUser() {
        RoleEntity adminRole = roleRepository.findByName("ADMIN")
            .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
        
        UserEntity admin = UserEntity.builder()
            .username("admin")
            .password(passwordEncoder.encode("Admin@123"))
            .email("admin@example.com")
            .roles(Set.of(adminRole))
            .build();
        
        userRepository.save(admin);
        log.info("Seeded admin user with username: admin");
    }

    private void seedTestUsers() {
        RoleEntity managerRole = roleRepository.findByName("MANAGER")
            .orElseThrow(() -> new RuntimeException("MANAGER role not found"));
        
        RoleEntity userRole = roleRepository.findByName("USER")
            .orElseThrow(() -> new RuntimeException("USER role not found"));
        
        // Manager user
        UserEntity manager = UserEntity.builder()
            .username("manager")
            .password(passwordEncoder.encode("Manager@123"))
            .email("manager@example.com")
            .roles(Set.of(managerRole))
            .build();
        
        // Regular user
        UserEntity regularUser = UserEntity.builder()
            .username("user")
            .password(passwordEncoder.encode("User@123"))
            .email("user@example.com")
            .roles(Set.of(userRole))
            .build();
        
        userRepository.saveAll(List.of(manager, regularUser));
        log.info("Seeded test users: manager, user");
    }

    private PermissionEntity createPermission(String name, String description) {
        return PermissionEntity.builder()
            .name(name)
            .description(description)
            .build();
    }
}