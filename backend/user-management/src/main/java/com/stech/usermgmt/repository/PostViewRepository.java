package com.stech.usermgmt.repository;

import com.stech.usermgmt.entity.PostViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostViewRepository extends JpaRepository<PostViewEntity, Long> {
    long countByPostId(Long postId);
}
