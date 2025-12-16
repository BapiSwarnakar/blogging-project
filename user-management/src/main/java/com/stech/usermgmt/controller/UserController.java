package com.stech.usermgmt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stech.common.security.annotation.RequirePermission;


@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    // Use permission slug from database
    @RequirePermission(authority = "USER_READ")
    @GetMapping("/me")
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok("User details");
    }

    // Use role name (Spring Security adds ROLE_ prefix automatically)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<String> getAllUsers() {
        return ResponseEntity.ok("All users");
    }

    @GetMapping("/internal/all")
    public ResponseEntity<String> getAllUsersInternal() {
        try {
            return ResponseEntity.status(500).body("All users");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to get all users");
        }
    }
    
}
