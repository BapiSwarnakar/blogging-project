package com.stech.usermgmt.service.impl;

import com.stech.usermgmt.dto.request.CategoryRequest;
import com.stech.usermgmt.dto.response.CategoryResponse;
import com.stech.usermgmt.entity.CategoryEntity;
import com.stech.usermgmt.exception.CustomResourceAlreadyExistsException;
import com.stech.usermgmt.repository.CategoryRepository;
import com.stech.usermgmt.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new CustomResourceAlreadyExistsException("Category with name '" + request.getName() + "' already exists");
        }
        CategoryEntity entity = CategoryEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        entity = categoryRepository.save(entity);
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        categoryRepository.findByName(request.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new CustomResourceAlreadyExistsException("Category with name '" + request.getName() + "' already exists");
                    }
                });

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity = categoryRepository.save(entity);
        return mapToResponse(entity);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByIsDeletedFalse().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        entity.setDeleted(true);
        categoryRepository.save(entity);
    }

    private CategoryResponse mapToResponse(CategoryEntity entity) {
        return CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
