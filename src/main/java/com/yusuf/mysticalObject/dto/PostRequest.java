package com.yusuf.mysticalObject.dto;

import com.yusuf.mysticalObject.entity.Tag;
import lombok.Data;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

@Data
public class PostRequest {
    private String title;
    private String description;
    private Long userId;
    private MultipartFile media;
    private String mediaUrl;
    
    private List<String> shapes;
    private List<String> colors;
    private List<String> materials;
    
    private Set<WikidataLabelRequest> wikiDataLabels;
    
    private int weight;
    private int height;
    private int width;
    private int depth;
    private Set<Tag> tags;
}
