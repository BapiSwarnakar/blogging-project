package com.stech.usermgmt.service;

import com.stech.usermgmt.dto.request.AnswerRequest;
import com.stech.usermgmt.dto.response.AnswerResponse;

import java.util.List;

public interface AnswerService {
    AnswerResponse createAnswer(AnswerRequest request, Long authorId);
    AnswerResponse updateAnswer(Long id, String content, Long userId);
    List<AnswerResponse> getAnswersByPostId(Long postId);
    void deleteAnswer(Long id, Long userId);
}
