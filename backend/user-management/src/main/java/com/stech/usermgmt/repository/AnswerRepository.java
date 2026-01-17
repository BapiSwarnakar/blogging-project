package com.stech.usermgmt.repository;

import com.stech.usermgmt.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
    List<AnswerEntity> findByPostIdAndIsDeletedFalse(Long postId);
    long countByPostIdAndIsDeletedFalse(Long postId);
}
