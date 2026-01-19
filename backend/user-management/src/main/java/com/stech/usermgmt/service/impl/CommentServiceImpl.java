package com.stech.usermgmt.service.impl;

import com.stech.usermgmt.dto.request.CommentRequest;
import com.stech.usermgmt.dto.response.CommentResponse;
import com.stech.usermgmt.entity.CommentEntity;
import com.stech.usermgmt.entity.PostEntity;
import com.stech.usermgmt.repository.CommentRepository;
import com.stech.usermgmt.repository.PostRepository;
import com.stech.usermgmt.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public CommentResponse createComment(CommentRequest request, Long authorId) {
        PostEntity post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        CommentEntity parentComment = null;
        if (request.getParentId() != null) {
            parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        CommentEntity comment = CommentEntity.builder()
                .content(request.getContent())
                .post(post)
                .authorId(authorId)
                .authorName(request.getAuthorName())
                .parentComment(parentComment)
                .build();

        comment = commentRepository.save(comment);
        return mapToResponse(comment);
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdAndParentCommentIdIsNullAndIsDeletedFalse(postId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthorId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    private CommentResponse mapToResponse(CommentEntity entity) {
        return CommentResponse.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .authorId(entity.getAuthorId())
                .authorName(entity.getAuthorName())
                .postId(entity.getPost().getId())
                .parentId(entity.getParentComment() != null ? entity.getParentComment().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .replies(entity.getReplies().stream()
                        .filter(reply -> !reply.isDeleted())
                        .map(this::mapToResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}
