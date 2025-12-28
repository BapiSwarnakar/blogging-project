package com.stech.authentication.service;

import java.util.Set;

import com.stech.authentication.dto.request.UserRequest;
import com.stech.authentication.entity.PermissionEntity;
import com.stech.authentication.entity.UserEntity;
import org.springframework.data.domain.Page;

public interface UserService {
    
    public Set<PermissionEntity> getAllPermissions(Long userId);
    public void assignDirectPermission(Long userId, Long permissionId);

    UserEntity createUser(UserRequest request);
    Page<UserEntity> getAllUsers(int page, int size, String sortBy, String sortDir, String search);
    UserEntity getUserById(Long id);
    UserEntity updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
}
