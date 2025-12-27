package com.stech.authentication.service;

import org.springframework.data.domain.Page;

import com.stech.authentication.dto.request.PermissionRequest;
import com.stech.authentication.entity.PermissionEntity;

public interface PermissionService {
    PermissionEntity createPermission(PermissionRequest request);
    PermissionEntity getPermissionById(Long id);
    Page<PermissionEntity> getAllPermissions(int page, int size, String sortBy, String sortDir, String search);
    PermissionEntity updatePermission(Long id, PermissionRequest request);
    void deletePermission(Long id);
}
