package com.stech.usermgmt.repository;

import com.stech.usermgmt.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByPostIdAndParentCommentIdIsNullAndIsDeletedFalse(Long postId);
    long countByPostIdAndIsDeletedFalse(Long postId);
}
