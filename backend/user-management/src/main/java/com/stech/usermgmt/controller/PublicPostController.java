package com.stech.usermgmt.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stech.common.library.GlobalApiResponse;
import com.stech.usermgmt.dto.response.CategoryResponse;
import com.stech.usermgmt.dto.response.CommentResponse;
import com.stech.usermgmt.dto.response.PostResponse;
import com.stech.usermgmt.entity.PostEntity;
import com.stech.usermgmt.service.CategoryService;
import com.stech.usermgmt.service.CommentService;
import com.stech.usermgmt.service.PostService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user/public")
@RequiredArgsConstructor
public class PublicPostController {

    private final PostService postService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    @GetMapping("/posts")
    public ResponseEntity<GlobalApiResponse.ApiResult<Page<PostResponse>>> getAllPosts(
            @RequestParam(required = false) PostEntity.PostType type,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<PostResponse> posts = postService.getAllPosts(type, search, pageable);
        return ResponseEntity.ok(GlobalApiResponse.success(posts, "Posts fetched successfully"));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<GlobalApiResponse.ApiResult<PostResponse>> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(GlobalApiResponse.success(postService.getPostById(id), "Post fetched successfully"));
    }

    @GetMapping("/categories")
    public ResponseEntity<GlobalApiResponse.ApiResult<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(GlobalApiResponse.success(categoryService.getAllCategories(), "Categories fetched successfully"));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<GlobalApiResponse.ApiResult<List<CommentResponse>>> getCommentsByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(GlobalApiResponse.success(commentService.getCommentsByPostId(postId), "Comments fetched successfully"));
    }

    @PostMapping("/posts/{id}/vote")
    public ResponseEntity<GlobalApiResponse.ApiResult<PostResponse>> votePost(
            @PathVariable Long id,
            @RequestParam Integer type,
            HttpServletRequest request) {
        Long userId = com.stech.common.security.util.SecurityUtils.getCurrentUserId();
        String ipAddress = request.getRemoteAddr();
        return ResponseEntity.ok(GlobalApiResponse.success(postService.votePost(id, userId, ipAddress, type), "Vote submitted successfully"));
    }

    @PostMapping("/posts/{id}/view")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> incrementView(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = com.stech.common.security.util.SecurityUtils.getCurrentUserId();
        String ipAddress = request.getRemoteAddr();
        postService.incrementView(id, userId, ipAddress);
        return ResponseEntity.ok(GlobalApiResponse.success(null, "View count incremented successfully"));
    }
}
