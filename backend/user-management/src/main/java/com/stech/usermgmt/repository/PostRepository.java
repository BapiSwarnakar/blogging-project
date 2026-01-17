package com.stech.usermgmt.repository;

import com.stech.usermgmt.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Page<PostEntity> findByIsDeletedFalse(Pageable pageable);
    
    Page<PostEntity> findByIsDeletedFalseAndType(PostEntity.PostType type, Pageable pageable);

    // Search by title, excerpt or content
    Page<PostEntity> findByIsDeletedFalseAndTitleContainingIgnoreCaseOrExcerptContainingIgnoreCase(
            String title, String excerpt, Pageable pageable);
            
    List<PostEntity> findByCategoryIdAndIsDeletedFalse(Long categoryId);
}
