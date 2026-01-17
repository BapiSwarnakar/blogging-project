package com.stech.usermgmt.dto.response;

import com.stech.usermgmt.entity.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String excerpt;
    private String content;
    private Long authorId;
    private CategoryResponse category;
    private String image;
    private PostEntity.PostType type;
    private Integer viewCount;
    private Integer voteCount;
    private Integer answerCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
