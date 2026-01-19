package com.stech.usermgmt.repository;

import com.stech.usermgmt.entity.PostEntity;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PostEntity p WHERE p.id = :id")
    java.util.Optional<PostEntity> findByIdWithLock(Long id);

    Page<PostEntity> findByIsDeletedFalse(Pageable pageable);
    
    Page<PostEntity> findByIsDeletedFalseAndType(PostEntity.PostType type, Pageable pageable);

    // Search by title, excerpt or content
    Page<PostEntity> findByIsDeletedFalseAndTitleContainingIgnoreCaseOrExcerptContainingIgnoreCase(
            String title, String excerpt, Pageable pageable);
            
    List<PostEntity> findByCategoryIdAndIsDeletedFalse(Long categoryId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PostEntity p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PostEntity p SET p.voteCount = p.voteCount + :voteDelta WHERE p.id = :id")
    void updateVoteCount(Long id, int voteDelta);
}
