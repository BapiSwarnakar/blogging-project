package com.stech.usermgmt.repository;

import com.stech.usermgmt.entity.AnswerVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerVoteRepository extends JpaRepository<AnswerVoteEntity, Long> {
    Optional<AnswerVoteEntity> findByAnswerIdAndUserId(Long answerId, Long userId);
}
