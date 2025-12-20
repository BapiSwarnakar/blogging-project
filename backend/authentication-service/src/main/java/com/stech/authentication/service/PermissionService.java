package com.stech.authentication.service;

import java.util.List;

import com.stech.authentication.dto.request.PermissionRequest;
import com.stech.authentication.entity.PermissionEntity;

public interface PermissionService {
    PermissionEntity createPermission(PermissionRequest request);
    PermissionEntity getPermissionById(Long id);
    List<PermissionEntity> getAllPermissions();
    PermissionEntity updatePermission(Long id, PermissionRequest request);
    void deletePermission(Long id);
}
