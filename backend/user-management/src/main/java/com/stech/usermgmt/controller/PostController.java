package com.stech.usermgmt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stech.common.library.GlobalApiResponse;
import com.stech.common.security.util.SecurityUtils;
import com.stech.usermgmt.dto.request.PostRequest;
import com.stech.usermgmt.dto.response.PostResponse;
import com.stech.usermgmt.service.PostService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user/blog/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<GlobalApiResponse.ApiResult<PostResponse>> createPost(@RequestBody PostRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(GlobalApiResponse.success(postService.createPost(request, userId), "Post created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<PostResponse>> updatePost(
            @PathVariable Long id,
            @RequestBody PostRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(GlobalApiResponse.success(postService.updatePost(id, request, userId), "Post updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> deletePost(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        postService.deletePost(id, userId);
        return ResponseEntity.ok(GlobalApiResponse.success(null, "Post deleted successfully"));
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<GlobalApiResponse.ApiResult<PostResponse>> votePost(
            @PathVariable Long id,
            @RequestParam Integer type,
            HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        String ipAddress = request.getRemoteAddr();
        return ResponseEntity.ok(GlobalApiResponse.success(postService.votePost(id, userId, ipAddress, type), "Vote submitted successfully"));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> incrementView(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        String ipAddress = request.getRemoteAddr();
        postService.incrementView(id, userId, ipAddress);
        return ResponseEntity.ok(GlobalApiResponse.success(null, "View count incremented successfully"));
    }

    @PostMapping("/{id}/bookmark")
    public ResponseEntity<GlobalApiResponse.ApiResult<PostResponse>> bookmarkPost(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(GlobalApiResponse.success(postService.toggleBookmark(id, userId), "Post bookmarked successfully"));
    }
}
