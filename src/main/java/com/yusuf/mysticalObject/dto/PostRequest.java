package com.yusuf.mysticalObject.dto;

import com.yusuf.mysticalObject.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class PostRequest {
    private String title;
    private String description;
    private List<String> tags;
    private User user;
}
