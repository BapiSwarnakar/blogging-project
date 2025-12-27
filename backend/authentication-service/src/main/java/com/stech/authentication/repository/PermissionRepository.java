package com.stech.authentication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stech.authentication.entity.PermissionEntity;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    Optional<PermissionEntity> findByName(String permName);

    boolean existsByName(String name);

    List<PermissionEntity> findByNameIn(List<String> names);

    Page<PermissionEntity> findByNameContainingIgnoreCaseOrSlugContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String name, String slug, String category, Pageable pageable);

}
