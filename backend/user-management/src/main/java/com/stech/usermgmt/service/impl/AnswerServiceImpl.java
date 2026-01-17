package com.stech.usermgmt.service.impl;

import com.stech.usermgmt.dto.request.AnswerRequest;
import com.stech.usermgmt.dto.response.AnswerResponse;
import com.stech.usermgmt.entity.AnswerEntity;
import com.stech.usermgmt.entity.PostEntity;
import com.stech.usermgmt.repository.AnswerRepository;
import com.stech.usermgmt.repository.PostRepository;
import com.stech.usermgmt.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public AnswerResponse createAnswer(AnswerRequest request, Long authorId) {
        PostEntity post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        AnswerEntity entity = AnswerEntity.builder()
                .post(post)
                .content(request.getContent())
                .authorId(authorId)
                .build();

        entity = answerRepository.save(entity);
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public AnswerResponse updateAnswer(Long id, String content, Long userId) {
        AnswerEntity entity = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        if (!entity.getAuthorId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this answer");
        }

        entity.setContent(content);
        entity = answerRepository.save(entity);
        return mapToResponse(entity);
    }

    @Override
    public List<AnswerResponse> getAnswersByPostId(Long postId) {
        return answerRepository.findByPostIdAndIsDeletedFalse(postId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAnswer(Long id, Long userId) {
        AnswerEntity entity = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        if (!entity.getAuthorId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this answer");
        }

        entity.setDeleted(true);
        answerRepository.save(entity);
    }

    private AnswerResponse mapToResponse(AnswerEntity entity) {
        return AnswerResponse.builder()
                .id(entity.getId())
                .postId(entity.getPost().getId())
                .content(entity.getContent())
                .authorId(entity.getAuthorId())
                .voteCount(entity.getVoteCount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
