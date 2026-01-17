package com.stech.usermgmt.controller;

import com.stech.common.permissions.AuthenticationServicePermissionList;
import com.stech.common.permissions.UserManagementServicePermissionList;
import com.stech.common.security.annotation.RequirePermission;
import com.stech.usermgmt.dto.request.CategoryRequest;
import com.stech.usermgmt.dto.response.CategoryResponse;
import com.stech.usermgmt.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/blog/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @RequirePermission(authority = UserManagementServicePermissionList.CATEGORY_WRITE)
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @RequirePermission(authority = UserManagementServicePermissionList.CATEGORY_UPDATE)
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @GetMapping
    @RequirePermission(authority = UserManagementServicePermissionList.CATEGORY_READ)
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @RequirePermission(authority = UserManagementServicePermissionList.CATEGORY_READ)
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(authority = UserManagementServicePermissionList.CATEGORY_DELETE)
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
