package com.stech.usermgmt.service;

import com.stech.usermgmt.dto.request.CommentRequest;
import com.stech.usermgmt.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(CommentRequest request, Long authorId);
    List<CommentResponse> getCommentsByPostId(Long postId);
    void deleteComment(Long commentId, Long userId);
}
