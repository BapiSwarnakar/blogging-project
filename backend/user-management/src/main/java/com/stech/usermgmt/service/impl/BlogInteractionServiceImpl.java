package com.stech.usermgmt.service.impl;

import com.stech.usermgmt.entity.*;
import com.stech.usermgmt.repository.*;
import com.stech.usermgmt.service.BlogInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogInteractionServiceImpl implements BlogInteractionService {

    private final PostRepository postRepository;
    private final PostViewRepository postViewRepository;
    private final PostVoteRepository postVoteRepository;

    @Override
    @Transactional
    public void recordPostView(Long postId, Long userId, String ipAddress) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostViewEntity view = PostViewEntity.builder()
                .post(post)
                .userId(userId)
                .ipAddress(ipAddress)
                .build();
        postViewRepository.save(view);

        // Update cached count in post
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void votePost(Long postId, Long userId, Integer voteType) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<PostVoteEntity> existingVote = postVoteRepository.findByPostIdAndUserId(postId, userId);

        if (existingVote.isPresent()) {
            PostVoteEntity vote = existingVote.get();
            // Remove old vote effect
            post.setVoteCount(post.getVoteCount() - vote.getVoteType());
            
            if (vote.getVoteType().equals(voteType)) {
                // If same vote type, user is un-voting
                postVoteRepository.delete(vote);
            } else {
                // Changing vote type
                vote.setVoteType(voteType);
                postVoteRepository.save(vote);
                post.setVoteCount(post.getVoteCount() + voteType);
            }
        } else {
            // New vote
            PostVoteEntity vote = PostVoteEntity.builder()
                    .post(post)
                    .userId(userId)
                    .voteType(voteType)
                    .build();
            postVoteRepository.save(vote);
            post.setVoteCount(post.getVoteCount() + voteType);
        }
        
        postRepository.save(post);
    }


}
