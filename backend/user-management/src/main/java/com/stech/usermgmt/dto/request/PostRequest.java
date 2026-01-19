package com.stech.usermgmt.dto.request;

import com.stech.usermgmt.entity.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    private String title;
    private String excerpt;
    private String content;
    private Long categoryId;
    private String image;
    private PostEntity.PostType type;
    private String authorName;
}
