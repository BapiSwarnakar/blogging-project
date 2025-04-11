package com.stech.authentication.service;

import java.util.Set;

import com.stech.authentication.entity.PermissionEntity;

public interface UserService {
    
    public Set<PermissionEntity> getAllPermissions(Long userId);
    public void assignDirectPermission(Long userId, Long permissionId);
}
