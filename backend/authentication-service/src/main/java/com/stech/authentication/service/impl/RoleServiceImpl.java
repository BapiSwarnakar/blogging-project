package com.stech.authentication.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import com.stech.authentication.dto.request.RoleRequest;
import com.stech.authentication.entity.PermissionEntity;
import com.stech.authentication.entity.RoleEntity;
import com.stech.authentication.exception.CustomOperationNotAllowedException;
import com.stech.authentication.exception.CustomResourceAlreadyExistsException;
import com.stech.authentication.exception.CustomResourceNotFoundException;
import com.stech.authentication.repository.PermissionRepository;
import com.stech.authentication.repository.RoleRepository;
import com.stech.authentication.service.RoleService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    
    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public RoleEntity createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new CustomResourceAlreadyExistsException("Role already exists with name: " + request.getName());
        }
        log.info("Creating role: {}", request);
        RoleEntity role = new RoleEntity();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setActive(request.isActive());
        role.setFullAccess(request.isFullAccess());

        if (request.getPermissionId() != null && !request.getPermissionId().isEmpty()) {
            Set<PermissionEntity> permissions = request.getPermissionId().stream()
                .map(permissionId -> permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new CustomResourceNotFoundException("Permission not found: " + permissionId)))
                .collect(Collectors.toSet());

            if (permissions.size() != request.getPermissionId().size()) {
                List<Long> missingPermissions = request.getPermissionId().stream()
                    .filter(id -> permissionRepository.findById(id).isEmpty())
                    .toList();
                    
                throw new CustomResourceNotFoundException(
                    "The following permissions were not found: " + String.join(", ", missingPermissions.stream()
                        .map(String::valueOf)
                        .toList())
                );
            }
            
            role.setPermissions(permissions);
        }

        RoleEntity savedRole = roleRepository.save(role);
        savedRole.setUsers(null); // Avoid loading users to prevent circular references
        savedRole.setPermissions(savedRole.getPermissions().stream()
            .map(permission -> {
                permission.setRoles(null); // Avoid loading roles to prevent circular references
                return permission;
            })
            .collect(Collectors.toSet()));
        return savedRole;
    }

    @Override
    public RoleEntity getRoleById(Long id) {
        RoleEntity role = roleRepository.findById(id)
            .orElseThrow(() -> new CustomResourceNotFoundException("Role not found with id: " + id));
        role.setUsers(null); // Avoid loading users to prevent circular references
        role.setPermissions(role.getPermissions().stream()
            .map(permission -> {
                permission.setRoles(null); // Avoid loading roles to prevent circular references
                return permission;
            })
            .collect(Collectors.toSet()));
        return role;
    }

    @Override
    public Page<RoleEntity> getAllRoles(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
            
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<RoleEntity> rolesPage = roleRepository.findAll(pageRequest);
        
        // Handle circular references for each role in the page
        rolesPage.getContent().forEach(role -> {
            role.setUsers(null);
            if (role.getPermissions() != null) {
                role.setPermissions(role.getPermissions().stream()
                    .map(permission -> {
                        permission.setRoles(null);
                        return permission;
                    })
                    .collect(Collectors.toSet()));
            }
        });
        
        return rolesPage;
    }

    @Override
    public RoleEntity updateRole(Long id, RoleRequest request) {
        RoleEntity role = getRoleById(id);
        
        if (request.getName() != null && !request.getName().equals(role.getName())) {
            if (roleRepository.existsByName(request.getName())) {
                throw new CustomResourceAlreadyExistsException("Role already exists with name: " + request.getName());
            }
            role.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        role.setActive(request.isActive());
        role.setFullAccess(request.isFullAccess());
        
        if (request.getPermissionId() != null) {
            Set<PermissionEntity> permissions = request.getPermissionId().stream()
                .map(permissionId -> permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new CustomResourceNotFoundException("Permission not found: " + permissionId)))
                .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }

        RoleEntity savedRole = roleRepository.save(role);
        savedRole.setUsers(null); // Avoid loading users to prevent circular references
        savedRole.setPermissions(savedRole.getPermissions().stream()
            .map(permission -> {
                permission.setRoles(null); // Avoid loading roles to prevent circular references
                return permission;
            })
            .collect(Collectors.toSet()));
        return savedRole;
    }

    @Override
    public void deleteRole(Long id) {
        RoleEntity role = roleRepository.findById(id)
            .orElseThrow(() -> new CustomResourceNotFoundException("Role not found with id: " + id));
        log.info("Role found with id: " + role.getUsers().size());
        if (role.getUsers() != null && !role.getUsers().isEmpty()) {
            throw new CustomOperationNotAllowedException(
                "Cannot delete role assigned to users. First unassign the role from all users."
            );
        }
        roleRepository.delete(role);
    }

    @Override
    public RoleEntity addPermissionToRole(Long roleId, Long permissionId) {
        RoleEntity role = getRoleById(roleId);
        PermissionEntity permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new CustomResourceNotFoundException("Permission not found with id: " + permissionId));
        
        if (role.getPermissions().contains(permission)) {
            throw new CustomResourceAlreadyExistsException(
                "Permission already assigned to this role"
            );
        }
        
        role.getPermissions().add(permission);
        RoleEntity savedRole = roleRepository.save(role);
        savedRole.setUsers(null); // Avoid loading users to prevent circular references
        savedRole.setPermissions(savedRole.getPermissions().stream()
            .map(permission1 -> {
                permission1.setRoles(null); // Avoid loading roles to prevent circular references
                return permission1;
            })
            .collect(Collectors.toSet()));
        return savedRole;
    }

    @Override
    public RoleEntity removePermissionFromRole(Long roleId, Long permissionId) {
        RoleEntity role = getRoleById(roleId);
        PermissionEntity permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new CustomResourceNotFoundException("Permission not found with id: " + permissionId));
        
        if (!role.getPermissions().contains(permission)) {
            throw new CustomResourceNotFoundException(
                "Permission not assigned to this role"
            );
        }
        
        role.getPermissions().remove(permission);
        return roleRepository.save(role);
    }
}
