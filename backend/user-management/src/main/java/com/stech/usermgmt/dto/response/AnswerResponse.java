package com.stech.usermgmt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResponse {
    private Long id;
    private Long postId;
    private String content;
    private Long authorId;
    private Integer voteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
