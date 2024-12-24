package com.yusuf.mysticalObject.dto;

import com.yusuf.mysticalObject.entity.Post;
import com.yusuf.mysticalObject.entity.Tag;
import com.yusuf.mysticalObject.entity.WikiDataLabel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String description;
    private String mediaUrl;
    private LocalDateTime createdAt;
    private UserResponse user;
    private List<String> shapes;
    private List<String> colors;
    private List<String> materials;
    private Set<WikiDataLabel> wikiDataLabels;
    private int weight;
    private int height;
    private int width;
    private int depth;
    private Set<Tag> tags;
    private int likeCount;
    private int commentCount;
    private boolean liked;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.mediaUrl = post.getMediaUrl();
        this.createdAt = post.getCreatedAt();
        this.user = new UserResponse(post.getUser());
        this.shapes = post.getShapes();
        this.colors = post.getColors();
        this.materials = post.getMaterials();
        this.wikiDataLabels = post.getWikiDataLabels();
        this.weight = post.getWeight();
        this.height = post.getHeight();
        this.width = post.getWidth();
        this.depth = post.getDepth();
        this.tags = post.getTags();
        this.commentCount = post.getComments().size();
    }
}
