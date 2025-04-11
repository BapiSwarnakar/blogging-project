package com.stech.authentication.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stech.authentication.entity.PermissionEntity;
import com.stech.authentication.exception.CustomOperationNotAllowedException;
import com.stech.authentication.exception.CustomResourceAlreadyExistsException;
import com.stech.authentication.exception.CustomResourceNotFoundException;
import com.stech.authentication.repository.PermissionRepository;
import com.stech.authentication.request.PermissionRequest;
import com.stech.authentication.service.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    PermissionServiceImpl(PermissionRepository permissionRepository){
        this.permissionRepository = permissionRepository;
    }

    // Create
    @Override
    public PermissionEntity createPermission(PermissionRequest request) {
        if (permissionRepository.existsByName(request.getName())) {
            throw new CustomResourceAlreadyExistsException("Permission already exists with name: " + request.getName());
        }

        PermissionEntity permission = new PermissionEntity();
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());

        return permissionRepository.save(permission);
    }

    // Read
    @Override
    public PermissionEntity getPermissionById(Long id) {
        return permissionRepository.findById(id)
            .orElseThrow(() -> new CustomResourceNotFoundException("Permission not found with id: " + id));
    }

    @Override
    public List<PermissionEntity> getAllPermissions() {
        return permissionRepository.findAll();
    }

    // Update
    @Override
    public PermissionEntity updatePermission(Long id, PermissionRequest request) {
        PermissionEntity permission = getPermissionById(id);
        
        if (request.getName() != null && !request.getName().equals(permission.getName())) {
            if (permissionRepository.existsByName(request.getName())) {
                throw new CustomResourceAlreadyExistsException("Permission already exists with name: " + request.getName());
            }
            permission.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            permission.setDescription(request.getDescription());
        }
        
        return permissionRepository.save(permission);
    }

    // Delete
    @Override
    public void deletePermission(Long id) {
        PermissionEntity permission = getPermissionById(id);
        if (!permission.getRoles().isEmpty() || !permission.getUsers().isEmpty()) {
            throw new CustomOperationNotAllowedException("Cannot delete permission assigned to roles or users");
        }
        permissionRepository.delete(permission);
    }
}
