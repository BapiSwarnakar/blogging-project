package com.stech.authentication.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;

import com.stech.authentication.dto.request.RoleRequest;
import com.stech.authentication.entity.RoleEntity;
import com.stech.authentication.exception.CustomBadRequestException;
import com.stech.authentication.exception.CustomOperationNotAllowedException;
import com.stech.authentication.exception.CustomResourceAlreadyExistsException;
import com.stech.authentication.exception.CustomResourceNotFoundException;
import com.stech.authentication.service.RoleService;
import com.stech.common.library.GlobalApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private static final String BAD_REQUEST_MESSAGE = "Bad Request";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String NOT_FOUND_MESSAGE = "Not Found";
    private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> createRole(@Valid @RequestBody RoleRequest request) {
        try {
            log.info("Creating new role: {}", request.getName());
            RoleEntity roleEntity = roleService.createRole(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalApiResponse.success(roleEntity, "Role created successfully"));
        } catch (CustomBadRequestException e) {
            log.error("Bad request during role creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GlobalApiResponse.error(BAD_REQUEST_MESSAGE, e.getMessage()));
        } catch (CustomResourceAlreadyExistsException e) {
            log.error("Resource already exists during role creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GlobalApiResponse.error(e.getMessage(),BAD_REQUEST_MESSAGE));
        } catch (Exception e) {
            log.error("Unexpected error creating role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> getRole(@PathVariable Long id) {
        try {
            log.info("Fetching role with ID: {}", id);
            Object role = roleService.getRoleById(id);
            return ResponseEntity.ok(GlobalApiResponse.success(role, "Role fetched successfully"));
        } catch (CustomResourceNotFoundException e) {
            log.error("Role not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(GlobalApiResponse.error(NOT_FOUND_MESSAGE, e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error fetching role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }

    @PreAuthorize("hasAuthority('ROLE_READ')")
    @GetMapping
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> getAllRoles(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @RequestParam(value = "search", required = false) String search
    ) {
        try {
            log.info("Fetching all roles with page: {}, size: {}, search: {}", page, size, search);
            Page<RoleEntity> roles = roleService.getAllRoles(page, size, sortBy, sortDir, search);
            
            GlobalApiResponse.PageInfo pageInfo = new GlobalApiResponse.PageInfo(
                roles.getSize(),
                roles.getNumber(),
                roles.getTotalElements(),
                roles.getTotalPages()
            );
            
            return ResponseEntity.ok(GlobalApiResponse.success(roles.getContent(), "Roles fetched successfully", pageInfo));
        } catch (Exception e) {
            log.error("Unexpected error fetching all roles: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {
        try {
            log.info("Updating role with ID: {}", id);
            Object role = roleService.updateRole(id, request);
            return ResponseEntity.ok(GlobalApiResponse.success(role, "Role updated successfully"));
        } catch (CustomResourceNotFoundException e) {
            log.error("Role not found for update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(GlobalApiResponse.error(NOT_FOUND_MESSAGE, e.getMessage()));
        } catch (CustomBadRequestException e) {
            log.error("Bad request during role update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GlobalApiResponse.error(BAD_REQUEST_MESSAGE, e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }

    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> deleteRole(@PathVariable Long id) {
        try {
            log.info("Deleting role with ID: {}", id);
            roleService.deleteRole(id);
            return ResponseEntity.ok(GlobalApiResponse.success(null, "Role deleted successfully"));
        } catch (CustomResourceNotFoundException e) {
            log.error("Role not found for deletion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(GlobalApiResponse.error(NOT_FOUND_MESSAGE, e.getMessage()));
        } catch (CustomOperationNotAllowedException e) {
            log.error("Operation not allowed for deletion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(GlobalApiResponse.error(e.getMessage(), "Operation not allowed"));
        } catch (Exception e) {
            log.error("Unexpected error deleting role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> addPermissionToRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        try {
            log.info("Adding permission {} to role {}", permissionId, roleId);
            Object result = roleService.addPermissionToRole(roleId, permissionId);
            return ResponseEntity.ok(GlobalApiResponse.success(result, "Permission added to role successfully"));
        } catch (CustomResourceNotFoundException e) {
            log.error("Resource not found adding permission to role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(GlobalApiResponse.error(NOT_FOUND_MESSAGE, e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error adding permission to role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> removePermissionFromRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        try {
            log.info("Removing permission {} from role {}", permissionId, roleId);
            Object result = roleService.removePermissionFromRole(roleId, permissionId);
            return ResponseEntity.ok(GlobalApiResponse.success(result, "Permission removed from role successfully"));
        } catch (CustomResourceNotFoundException e) {
            log.error("Resource not found removing permission from role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(GlobalApiResponse.error(NOT_FOUND_MESSAGE, e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error removing permission from role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }
}