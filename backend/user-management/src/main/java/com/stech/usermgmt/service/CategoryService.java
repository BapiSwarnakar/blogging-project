package com.stech.usermgmt.service;

import com.stech.usermgmt.dto.request.CategoryRequest;
import com.stech.usermgmt.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
    void deleteCategory(Long id);
}
