package com.stech.usermgmt.service;

import com.stech.usermgmt.dto.request.PostRequest;
import com.stech.usermgmt.dto.response.PostResponse;
import com.stech.usermgmt.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    PostResponse createPost(PostRequest request, Long authorId);
    PostResponse updatePost(Long id, PostRequest request, Long userId);
    PostResponse getPostById(Long id);
    Page<PostResponse> getAllPosts(PostEntity.PostType type, String search, Pageable pageable);
    void deletePost(Long id, Long userId);
    PostResponse votePost(Long id, Long userId, String ipAddress, Integer voteType);
    void incrementView(Long id, Long userId, String ipAddress);
    PostResponse toggleBookmark(Long id, Long userId);
}
