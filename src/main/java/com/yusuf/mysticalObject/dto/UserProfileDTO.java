package com.yusuf.mysticalObject.dto;

import com.yusuf.mysticalObject.entity.Comment;
import com.yusuf.mysticalObject.entity.Post;
import lombok.Data;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Data
@Builder
public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "comments"})
    private List<Post> recentPosts;
    
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "replies"})
    private List<Comment> recentComments;
} 