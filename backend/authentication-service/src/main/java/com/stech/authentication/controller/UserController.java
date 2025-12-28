package com.stech.authentication.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stech.authentication.dto.request.UserRequest;
import com.stech.authentication.entity.UserEntity;
import com.stech.authentication.exception.CustomResourceNotFoundException;
import com.stech.authentication.service.UserService;
import com.stech.common.library.GlobalApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource Not Found";
    private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";

    @PostMapping
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> addUser(@Valid @RequestBody UserRequest request) {
        try {
            log.info("Adding new user: {}", request.getEmail());
            UserEntity user = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GlobalApiResponse.success(user, "User added successfully"));
        } catch (Exception e) {
            log.error("Error adding user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @RequestParam(value = "search", required = false) String search) {
        try {
            log.info("Fetching all users with page: {}, size: {}, search: {}", page, size, search);
            Page<UserEntity> users = userService.getAllUsers(page, size, sortBy, sortDir, search);

            GlobalApiResponse.PageInfo pageInfo = new GlobalApiResponse.PageInfo(
                    users.getSize(),
                    users.getNumber(),
                    users.getTotalElements(),
                    users.getTotalPages()
            );

            return ResponseEntity.ok(GlobalApiResponse.success(users.getContent(), "Users fetched successfully", pageInfo));
        } catch (Exception e) {
            log.error("Error fetching users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> getUserById(@PathVariable Long id) {
        try {
            log.info("Fetching user with ID: {}", id);
            UserEntity user = userService.getUserById(id);
            return ResponseEntity.ok(GlobalApiResponse.success(user, "User fetched successfully"));
        } catch (CustomResourceNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GlobalApiResponse.error(RESOURCE_NOT_FOUND_MESSAGE, e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        try {
            log.info("Updating user with ID: {}", id);
            UserEntity user = userService.updateUser(id, request);
            return ResponseEntity.ok(GlobalApiResponse.success(user, "User updated successfully"));
        } catch (CustomResourceNotFoundException e) {
            log.error("User not found for update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GlobalApiResponse.error(RESOURCE_NOT_FOUND_MESSAGE, e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> deleteUser(@PathVariable Long id) {
        try {
            log.info("Deleting user with ID: {}", id);
            userService.deleteUser(id);
            return ResponseEntity.ok(GlobalApiResponse.success(null, "User deleted successfully"));
        } catch (CustomResourceNotFoundException e) {
            log.error("User not found for deletion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GlobalApiResponse.error(RESOURCE_NOT_FOUND_MESSAGE, e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, UNEXPECTED_ERROR_MESSAGE));
        }
    }
}
