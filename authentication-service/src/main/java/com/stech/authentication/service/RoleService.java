package com.stech.authentication.service;

import java.util.List;

import com.stech.authentication.entity.RoleEntity;
import com.stech.authentication.request.RoleRequest;

public interface RoleService {
    RoleEntity createRole(RoleRequest request);
    RoleEntity getRoleById(Long id);
    List<RoleEntity> getAllRoles();
    RoleEntity updateRole(Long id, RoleRequest request);
    void deleteRole(Long id);
    RoleEntity addPermissionToRole(Long roleId, Long permissionId);
    RoleEntity removePermissionFromRole(Long roleId, Long permissionId);
}   
