package com.stech.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stech.authentication.external.ExternalUserService;
import com.stech.common.library.GlobalApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth/internal")
public class InternalCommunicationController {

    private final ExternalUserService userService;

    InternalCommunicationController(
        ExternalUserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/call-user-management-service")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> callUserManagementService() {
        try {
            String response = userService.getUserAll();
            return ResponseEntity.ok(GlobalApiResponse.success(response, "User authenticated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(GlobalApiResponse.error("User authentication failed", e.getMessage()));
        }
    }

    
}
