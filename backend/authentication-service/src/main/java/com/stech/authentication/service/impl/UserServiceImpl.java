package com.stech.authentication.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.stech.authentication.entity.PermissionEntity;
import com.stech.authentication.entity.UserEntity;
import com.stech.authentication.repository.PermissionRepository;
import com.stech.authentication.repository.UserRepository;
import com.stech.authentication.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;


    UserServiceImpl(UserRepository userRepository, PermissionRepository permissionRepository){
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    public Set<PermissionEntity> getAllPermissions(Long userId) {
        Set<PermissionEntity> rolePermissions = userRepository.findPermissionsByRoles(userId);
        Set<PermissionEntity> directPermissions = userRepository.findDirectPermissions(userId);

        Set<PermissionEntity> allPermissions = new HashSet<>();
        allPermissions.addAll(rolePermissions);
        allPermissions.addAll(directPermissions);

        return allPermissions;
    }

    public void assignDirectPermission(Long userId, Long permissionId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        PermissionEntity permission = permissionRepository.findById(permissionId).orElseThrow(() -> new RuntimeException("Permission not found"));
        user.getDirectPermissions().add(permission);
        userRepository.save(user);
    }
}
