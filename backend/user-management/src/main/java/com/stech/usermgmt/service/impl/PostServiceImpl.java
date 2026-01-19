package com.stech.usermgmt.service.impl;

import com.stech.common.security.util.SecurityUtils;
import com.stech.usermgmt.dto.request.PostRequest;
import com.stech.usermgmt.dto.response.CategoryResponse;
import com.stech.usermgmt.dto.response.PostResponse;
import com.stech.usermgmt.entity.CategoryEntity;
import com.stech.usermgmt.entity.PostEntity;
import com.stech.usermgmt.entity.PostViewEntity;
import com.stech.usermgmt.entity.PostVoteEntity;
import com.stech.usermgmt.entity.PostBookmarkEntity;
import com.stech.usermgmt.repository.CategoryRepository;
import com.stech.usermgmt.repository.CommentRepository;
import com.stech.usermgmt.repository.PostRepository;
import com.stech.usermgmt.repository.PostViewRepository;
import com.stech.usermgmt.repository.PostVoteRepository;
import com.stech.usermgmt.repository.PostBookmarkRepository;
import com.stech.usermgmt.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final PostVoteRepository postVoteRepository;
    private final PostViewRepository postViewRepository;
    private final PostBookmarkRepository postBookmarkRepository;

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
                .authorName(request.getAuthorName() != null ? request.getAuthorName() : "Anonymous")
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

        if (entity.getAuthorId() == null || !entity.getAuthorId().equals(userId)) {
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
        if (request.getAuthorName() != null) {
            entity.setAuthorName(request.getAuthorName());
        }

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

        if (entity.getAuthorId() == null || !entity.getAuthorId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this post");
        }

        entity.setDeleted(true);
        postRepository.save(entity);
    }

    @Override
    @Transactional
    public PostResponse votePost(Long id, Long userId, String ipAddress, Integer voteType) {
        PostEntity post = postRepository.findByIdWithLock(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostVoteEntity existingVote;
        if (userId != null) {
            existingVote = postVoteRepository.findByPostIdAndUserId(id, userId).orElse(null);
        } else {
            existingVote = postVoteRepository.findByPostIdAndIpAddressAndUserIdIsNull(id, ipAddress).orElse(null);
        }

        if (existingVote != null) {
            Integer existingVoteType = existingVote.getVoteType();
            if (existingVoteType != null && existingVoteType.equals(voteType)) {
                // Remove vote if clicking same button
                postVoteRepository.delete(existingVote);
                postRepository.updateVoteCount(id, -voteType);
            } else if (existingVoteType != null) {
                // Change vote type
                existingVote.setVoteType(voteType);
                postVoteRepository.save(existingVote);
                postRepository.updateVoteCount(id, -existingVoteType + voteType);
            }
        } else {
            // New vote
            PostVoteEntity newVote = PostVoteEntity.builder()
                    .post(post)
                    .userId(userId)
                    .ipAddress(ipAddress)
                    .voteType(voteType)
                    .build();
            postVoteRepository.save(newVote);
            postRepository.updateVoteCount(id, voteType);
        }

        // Return updated post
        PostEntity updatedPost = postRepository.findByIdWithLock(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToResponse(updatedPost);
    }

    @Override
    @Transactional
    public void incrementView(Long id, Long userId, String ipAddress) {
        PostEntity post = postRepository.findByIdWithLock(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostViewEntity view = PostViewEntity.builder()
                .post(post)
                .userId(userId)
                .ipAddress(ipAddress)
                .build();
        postViewRepository.save(view);

        postRepository.incrementViewCount(id);
    }

    @Override
    @Transactional
    public PostResponse toggleBookmark(Long id, Long userId) {
        PostEntity post = postRepository.findByIdWithLock(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<PostBookmarkEntity> existingBookmark = postBookmarkRepository.findByPostIdAndUserId(id, userId);

        if (existingBookmark.isPresent()) {
            postBookmarkRepository.delete(existingBookmark.get());
        } else {
            PostBookmarkEntity newBookmark = PostBookmarkEntity.builder()
                    .post(post)
                    .userId(userId)
                    .build();
            postBookmarkRepository.save(newBookmark);
        }

        return mapToResponse(post);
    }

    private PostResponse mapToResponse(PostEntity entity) {
        long commentCount = commentRepository.countByPostIdAndIsDeletedFalse(entity.getId());
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isBookmarked = false;
        if (currentUserId != null) {
            isBookmarked = postBookmarkRepository.existsByPostIdAndUserId(entity.getId(), currentUserId);
        }

        return PostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .excerpt(entity.getExcerpt())
                .content(entity.getContent())
                .authorId(entity.getAuthorId())
                .authorName(entity.getAuthorName())
                .category(CategoryResponse.builder()
                        .id(entity.getCategory().getId())
                        .name(entity.getCategory().getName())
                        .description(entity.getCategory().getDescription())
                        .build())
                .image(entity.getImage())
                .type(entity.getType())
                .viewCount(entity.getViewCount())
                .voteCount(entity.getVoteCount())
                .commentCount((int) commentCount)
                .isBookmarked(isBookmarked)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
