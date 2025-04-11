package com.stech.authentication.service;

import java.util.List;

import com.stech.authentication.entity.PermissionEntity;
import com.stech.authentication.request.PermissionRequest;

public interface PermissionService {
    PermissionEntity createPermission(PermissionRequest request);
    PermissionEntity getPermissionById(Long id);
    List<PermissionEntity> getAllPermissions();
    PermissionEntity updatePermission(Long id, PermissionRequest request);
    void deletePermission(Long id);
}
