package com.stech.usermgmt.service;

public interface BlogInteractionService {
    void recordPostView(Long postId, Long userId, String ipAddress);
    void votePost(Long postId, Long userId, Integer voteType);
}
