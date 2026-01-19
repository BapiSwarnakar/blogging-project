package com.stech.usermgmt.repository;

import com.stech.usermgmt.entity.PostVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVoteEntity, Long> {
    Optional<PostVoteEntity> findByPostIdAndUserId(Long postId, Long userId);
    Optional<PostVoteEntity> findByPostIdAndIpAddress(Long postId, String ipAddress);
    Optional<PostVoteEntity> findByPostIdAndIpAddressAndUserIdIsNull(Long postId, String ipAddress);
}
