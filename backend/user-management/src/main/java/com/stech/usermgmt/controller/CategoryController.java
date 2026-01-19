package com.stech.usermgmt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stech.common.library.GlobalApiResponse;
import com.stech.common.permissions.UserManagementServicePermissionList;
import com.stech.common.security.annotation.RequirePermission;
import com.stech.usermgmt.dto.request.CategoryRequest;
import com.stech.usermgmt.dto.response.CategoryResponse;
import com.stech.usermgmt.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user/blog/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @RequirePermission(authority = UserManagementServicePermissionList.CATEGORY_WRITE)
    public ResponseEntity<GlobalApiResponse.ApiResult<CategoryResponse>> createCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok(GlobalApiResponse.success(categoryService.createCategory(request), "Category created successfully"));
    }

    @PutMapping("/{id}")
    @RequirePermission(authority = UserManagementServicePermissionList.CATEGORY_UPDATE)
    public ResponseEntity<GlobalApiResponse.ApiResult<CategoryResponse>> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(GlobalApiResponse.success(categoryService.updateCategory(id, request), "Category updated successfully"));
    }

    @GetMapping("/{id}")
    @RequirePermission(authority = UserManagementServicePermissionList.CATEGORY_READ)
    public ResponseEntity<GlobalApiResponse.ApiResult<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(GlobalApiResponse.success(categoryService.getCategoryById(id), "Category fetched successfully"));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(authority = UserManagementServicePermissionList.CATEGORY_DELETE)
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(GlobalApiResponse.success(null, "Category deleted successfully"));
    }
}
