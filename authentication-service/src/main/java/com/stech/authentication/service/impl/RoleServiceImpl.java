package com.stech.authentication.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stech.authentication.entity.PermissionEntity;
import com.stech.authentication.entity.RoleEntity;
import com.stech.authentication.exception.CustomOperationNotAllowedException;
import com.stech.authentication.exception.CustomResourceAlreadyExistsException;
import com.stech.authentication.exception.CustomResourceNotFoundException;
import com.stech.authentication.repository.PermissionRepository;
import com.stech.authentication.repository.RoleRepository;
import com.stech.authentication.request.RoleRequest;
import com.stech.authentication.service.RoleService;

import jakarta.transaction.Transactional;

@Service
@Transactional
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

        RoleEntity role = new RoleEntity();
        role.setName(request.getName());
        role.setDescription(request.getDescription());

        if (request.getPermissionNames() != null && !request.getPermissionNames().isEmpty()) {
            Set<PermissionEntity> permissions = request.getPermissionNames().stream()
                .map(permissionName -> permissionRepository.findByName(permissionName)
                    .orElseThrow(() -> new CustomResourceNotFoundException("Permission not found: " + permissionName)))
                .collect(Collectors.toSet());
            
            if (permissions.size() != request.getPermissionNames().size()) {
                List<String> missingPermissions = request.getPermissionNames().stream()
                    .filter(name -> permissionRepository.findByName(name).isEmpty())
                    .toList();
                    
                throw new CustomResourceNotFoundException(
                    "The following permissions were not found: " + String.join(", ", missingPermissions)
                );
            }
            
            role.setPermissions(permissions);
        }

        return roleRepository.save(role);
    }

    @Override
    public RoleEntity getRoleById(Long id) {
        return roleRepository.findById(id)
            .orElseThrow(() -> new CustomResourceNotFoundException("Role not found with id: " + id));
    }

    @Override
    public List<RoleEntity> getAllRoles() {
        return roleRepository.findAll();
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
        
        if (request.getPermissionNames() != null) {
            Set<PermissionEntity> permissions = request.getPermissionNames().stream()
                .map(permissionName -> permissionRepository.findByName(permissionName)
                    .orElseThrow(() -> new CustomResourceNotFoundException("Permission not found: " + permissionName)))
                .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }
        
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long id) {
        RoleEntity role = getRoleById(id);
        if (!role.getUsers().isEmpty()) {
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
        return roleRepository.save(role);
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
