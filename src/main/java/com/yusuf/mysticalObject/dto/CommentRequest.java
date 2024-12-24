package com.yusuf.mysticalObject.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long postId;
    private Long userId;
    private Long parentId;
} 