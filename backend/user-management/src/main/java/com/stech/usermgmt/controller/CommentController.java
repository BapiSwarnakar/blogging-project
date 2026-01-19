package com.stech.usermgmt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stech.common.library.GlobalApiResponse;
import com.stech.usermgmt.dto.request.CommentRequest;
import com.stech.usermgmt.dto.response.CommentResponse;
import com.stech.usermgmt.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<GlobalApiResponse.ApiResult<CommentResponse>> createComment(
            @RequestBody CommentRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(GlobalApiResponse.success(commentService.createComment(request, userId), "Comment created successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> deleteComment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        commentService.deleteComment(id, userId);
        return ResponseEntity.ok(GlobalApiResponse.success(null, "Comment deleted successfully"));
    }
}
