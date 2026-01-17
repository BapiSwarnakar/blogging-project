package com.stech.usermgmt.controller;

import com.stech.usermgmt.service.BlogInteractionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/blog/interaction")
@RequiredArgsConstructor
public class BlogInteractionController {

    private final BlogInteractionService interactionService;

    @PostMapping("/post/{postId}/view")
    public ResponseEntity<Void> recordView(
            @PathVariable Long postId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        interactionService.recordPostView(postId, userId, ipAddress);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/post/{postId}/vote")
    public ResponseEntity<Void> votePost(
            @PathVariable Long postId,
            @RequestParam Integer type, // 1 or -1
            @RequestHeader("X-User-Id") Long userId) {
        interactionService.votePost(postId, userId, type);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/answer/{answerId}/vote")
    public ResponseEntity<Void> voteAnswer(
            @PathVariable Long answerId,
            @RequestParam Integer type, // 1 or -1
            @RequestHeader("X-User-Id") Long userId) {
        interactionService.voteAnswer(answerId, userId, type);
        return ResponseEntity.ok().build();
    }
}
