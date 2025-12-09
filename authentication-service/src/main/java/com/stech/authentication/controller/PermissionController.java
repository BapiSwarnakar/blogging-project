package com.stech.authentication.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.stech.authentication.dto.request.PermissionRequest;
import com.stech.authentication.entity.PermissionEntity;
import com.stech.authentication.exception.CustomBadRequestException;
import com.stech.authentication.exception.CustomResourceNotFoundException;
import com.stech.authentication.service.PermissionService;
import com.stech.common.library.GlobalApiResponse;
import com.stech.common.permissions.AuthenticationServicePermissionList;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth/permissions")
@Slf4j
public class PermissionController {

    private static final String BAD_REQUEST_MESSAGE = "Bad Request";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String NOT_FOUND_MESSAGE = "Not Found";
    private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";

    private final PermissionService permissionService;

    PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> createPermission(@RequestBody PermissionRequest request) {
        try {
            log.info("Creating new permission");
            PermissionEntity permission = permissionService.createPermission(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GlobalApiResponse.success(permission, "Permission created successfully"));
            
        } catch (CustomBadRequestException e) {
            log.error("Bad request during permission creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GlobalApiResponse.error(BAD_REQUEST_MESSAGE, e.getMessage()));
            
        } catch (Exception e) {
            log.error("Unexpected error during permission creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }

    @PreAuthorize("hasAuthority('" + AuthenticationServicePermissionList.PERMISSION_READ + "')")
    @GetMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> getPermission(@PathVariable Long id) {
        try {
            log.info("Fetching permission with ID: {}", id);
            PermissionEntity permission = permissionService.getPermissionById(id);
            return ResponseEntity.ok(GlobalApiResponse.success(permission, "Permission fetched successfully"));
            
        } catch (CustomResourceNotFoundException e) {
            log.error("Permission not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GlobalApiResponse.error(NOT_FOUND_MESSAGE, e.getMessage()));
            
        } catch (Exception e) {
            log.error("Unexpected error fetching permission: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }

    @GetMapping
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> getAllPermissions() {
        try {
            log.info("Fetching all permissions");
            List<PermissionEntity> permissions = permissionService.getAllPermissions();
            return ResponseEntity.ok(GlobalApiResponse.success(permissions, "Permissions fetched successfully"));
            
        } catch (Exception e) {
            log.error("Unexpected error fetching all permissions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> updatePermission(@PathVariable Long id, @RequestBody PermissionRequest request) {
        try {
            log.info("Updating permission with ID: {}", id);
            PermissionEntity permission = permissionService.updatePermission(id, request);
            return ResponseEntity.ok(GlobalApiResponse.success(permission, "Permission updated successfully"));
            
        } catch (CustomResourceNotFoundException e) {
            log.error("Permission not found for update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GlobalApiResponse.error(NOT_FOUND_MESSAGE, e.getMessage()));
            
        } catch (CustomBadRequestException e) {
            log.error("Bad request during permission update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GlobalApiResponse.error(BAD_REQUEST_MESSAGE, e.getMessage()));
            
        } catch (Exception e) {
            log.error("Unexpected error updating permission: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> deletePermission(@PathVariable Long id) {
        try {
            log.info("Deleting permission with ID: {}", id);
            permissionService.deletePermission(id);
            return ResponseEntity.ok(GlobalApiResponse.success(null, "Permission deleted successfully"));
            
        } catch (CustomResourceNotFoundException e) {
            log.error("Permission not found for deletion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GlobalApiResponse.error(NOT_FOUND_MESSAGE, e.getMessage()));
            
        } catch (Exception e) {
            log.error("Unexpected error deleting permission: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }
}
