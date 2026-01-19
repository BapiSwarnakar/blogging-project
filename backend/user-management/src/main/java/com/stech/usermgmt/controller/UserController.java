package com.stech.usermgmt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stech.common.library.GlobalApiResponse;


@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    // Use permission slug from database
    @GetMapping("/me")
    public ResponseEntity<GlobalApiResponse.ApiResult<String>> getUser() {
        return ResponseEntity.ok(GlobalApiResponse.success("User details", "User fetched successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<GlobalApiResponse.ApiResult<String>> getAllUsers() {
        return ResponseEntity.ok(GlobalApiResponse.success("All users", "Users fetched successfully"));
    }

    @GetMapping("/internal/all")
    public ResponseEntity<GlobalApiResponse.ApiResult<String>> getAllUsersInternal() {
        return ResponseEntity.ok(GlobalApiResponse.success("All users", "Users fetched successfully"));
    }
    
}
