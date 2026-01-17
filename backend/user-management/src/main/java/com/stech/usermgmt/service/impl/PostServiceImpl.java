package com.stech.usermgmt.service.impl;

import com.stech.usermgmt.dto.request.PostRequest;
import com.stech.usermgmt.dto.response.CategoryResponse;
import com.stech.usermgmt.dto.response.PostResponse;
import com.stech.usermgmt.entity.CategoryEntity;
import com.stech.usermgmt.entity.PostEntity;
import com.stech.usermgmt.repository.AnswerRepository;
import com.stech.usermgmt.repository.CategoryRepository;
import com.stech.usermgmt.repository.PostRepository;
import com.stech.usermgmt.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final AnswerRepository answerRepository;

    @Override
    @Transactional
    public PostResponse createPost(PostRequest request, Long authorId) {
        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        PostEntity entity = PostEntity.builder()
                .title(request.getTitle())
                .excerpt(request.getExcerpt())
                .content(request.getContent())
                .authorId(authorId)
                .category(category)
                .image(request.getImage())
                .type(request.getType() != null ? request.getType() : PostEntity.PostType.PUBLIC)
                .build();

        entity = postRepository.save(entity);
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public PostResponse updatePost(Long id, PostRequest request, Long userId) {
        PostEntity entity = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if user is author (Add admin bypass if needed in controller/security)
        if (!entity.getAuthorId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this post");
        }

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        entity.setTitle(request.getTitle());
        entity.setExcerpt(request.getExcerpt());
        entity.setContent(request.getContent());
        entity.setCategory(category);
        entity.setImage(request.getImage());
        entity.setType(request.getType());

        entity = postRepository.save(entity);
        return mapToResponse(entity);
    }

    @Override
    public PostResponse getPostById(Long id) {
        PostEntity entity = postRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // Note: For private posts, the controller should handle authentication check
        return mapToResponse(entity);
    }

    @Override
    public Page<PostResponse> getAllPosts(PostEntity.PostType type, String search, Pageable pageable) {
        Page<PostEntity> entityPage;
        
        if (search != null && !search.isEmpty()) {
            entityPage = postRepository.findByIsDeletedFalseAndTitleContainingIgnoreCaseOrExcerptContainingIgnoreCase(
                    search, search, pageable);
        } else if (type != null) {
            entityPage = postRepository.findByIsDeletedFalseAndType(type, pageable);
        } else {
            entityPage = postRepository.findByIsDeletedFalse(pageable);
        }

        return entityPage.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void deletePost(Long id, Long userId) {
        PostEntity entity = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!entity.getAuthorId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this post");
        }

        entity.setDeleted(true);
        postRepository.save(entity);
    }

    private PostResponse mapToResponse(PostEntity entity) {
        long answerCount = answerRepository.countByPostIdAndIsDeletedFalse(entity.getId());
        
        return PostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .excerpt(entity.getExcerpt())
                .content(entity.getContent())
                .authorId(entity.getAuthorId())
                .category(CategoryResponse.builder()
                        .id(entity.getCategory().getId())
                        .name(entity.getCategory().getName())
                        .build())
                .image(entity.getImage())
                .type(entity.getType())
                .viewCount(entity.getViewCount())
                .voteCount(entity.getVoteCount())
                .answerCount((int) answerCount)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
