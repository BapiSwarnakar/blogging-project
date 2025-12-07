package com.stech.usermgmt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    // Use permission slug from database
    @PreAuthorize("hasAuthority('USER_READ')")
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
    
}
