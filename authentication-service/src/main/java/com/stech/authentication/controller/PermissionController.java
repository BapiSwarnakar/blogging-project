package com.stech.authentication.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stech.authentication.entity.PermissionEntity;
import com.stech.authentication.request.PermissionRequest;
import com.stech.authentication.service.PermissionService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/auth/permissions")
public class PermissionController {

    private final PermissionService permissionService;
    private final HttpServletRequest request;

    PermissionController(PermissionService permissionService, HttpServletRequest request) {
        this.permissionService = permissionService;
        this.request = request;
    }

    @PostMapping
    public ResponseEntity<PermissionEntity> createPermission(@RequestBody PermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.createPermission(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionEntity> getPermission(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    @GetMapping
    public ResponseEntity<List<PermissionEntity>> getAllPermissions() {
        System.out.println("Request URL: " + request.getHeader("ipAddress"));
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionEntity> updatePermission(@PathVariable Long id, @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(permissionService.updatePermission(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
