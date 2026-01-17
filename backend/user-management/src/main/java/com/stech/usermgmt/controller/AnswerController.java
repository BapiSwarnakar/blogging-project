package com.stech.usermgmt.controller;

import com.stech.usermgmt.dto.request.AnswerRequest;
import com.stech.usermgmt.dto.response.AnswerResponse;
import com.stech.usermgmt.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/blog/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    public ResponseEntity<AnswerResponse> createAnswer(
            @RequestBody AnswerRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(answerService.createAnswer(request, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnswerResponse> updateAnswer(
            @PathVariable Long id,
            @RequestBody String content,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(answerService.updateAnswer(id, content, userId));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<AnswerResponse>> getAnswersByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(answerService.getAnswersByPostId(postId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        answerService.deleteAnswer(id, userId);
        return ResponseEntity.noContent().build();
    }
}
