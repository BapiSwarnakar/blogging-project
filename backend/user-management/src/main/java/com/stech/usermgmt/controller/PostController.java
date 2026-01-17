package com.stech.usermgmt.controller;

import com.stech.common.permissions.UserManagementServicePermissionList;
import com.stech.common.security.annotation.RequirePermission;
import com.stech.usermgmt.dto.request.PostRequest;
import com.stech.usermgmt.dto.response.PostResponse;
import com.stech.usermgmt.entity.PostEntity;
import com.stech.usermgmt.service.PostService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/blog/posts")
@Slf4j
public class PostController {

    private final PostService postService;

    PostController(
        PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @RequirePermission(authority = UserManagementServicePermissionList.POST_WRITE)
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostRequest request, 
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(postService.createPost(request, userId));
    }

    @PutMapping("/{id}")
    @RequirePermission(authority = UserManagementServicePermissionList.POST_UPDATE)
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id, 
            @RequestBody PostRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(postService.updatePost(id, request, userId));
    }

    @GetMapping("/{id}")
    @RequirePermission(authority = UserManagementServicePermissionList.POST_READ)
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping
    @RequirePermission(authority = UserManagementServicePermissionList.POST_READ)
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(required = false) PostEntity.PostType type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(postService.getAllPosts(type, search, pageable));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(authority = UserManagementServicePermissionList.POST_DELETE)
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        postService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }
}
