package com.stech.authentication.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.stech.authentication.dto.request.RoleRequest;
import com.stech.authentication.entity.RoleEntity;

public interface RoleService {
    RoleEntity createRole(RoleRequest request);
    RoleEntity getRoleById(Long id);
    Page<RoleEntity> getAllRoles(int page, int size, String sortBy, String sortDir);
    RoleEntity updateRole(Long id, RoleRequest request);
    void deleteRole(Long id);
    RoleEntity addPermissionToRole(Long roleId, Long permissionId);
    RoleEntity removePermissionFromRole(Long roleId, Long permissionId);
}   
