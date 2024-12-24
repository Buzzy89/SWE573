package com.yusuf.mysticalObject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yusuf.mysticalObject.dto.PageResponse;
import com.yusuf.mysticalObject.dto.PostRequest;
import com.yusuf.mysticalObject.dto.WikidataLabelRequest;
import com.yusuf.mysticalObject.entity.Post;
import com.yusuf.mysticalObject.entity.Tag;
import com.yusuf.mysticalObject.entity.User;
import com.yusuf.mysticalObject.service.PostService;
import com.yusuf.mysticalObject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;


    @PostMapping("/create")
    public ResponseEntity<Post> createPost(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "media", required = false) MultipartFile media,
            @RequestParam("shapes") String shapesJson,
            @RequestParam("colors") String colorsJson,
            @RequestParam("materials") String materialsJson,
            @RequestParam("wikiDataLabels") String wikiDataLabelsJson,
            @RequestParam("weight") int weight,
            @RequestParam("height") int height,
            @RequestParam("width") int width,
            @RequestParam("depth") int depth,
            @RequestParam("tags") String tagsJson,
            @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
        try {
            log.info("Create post request received. User: {}, Title: {}", 
                userDetails != null ? userDetails.getUsername() : "null", 
                title);
            
            if (userDetails == null) {
                log.error("Unauthorized access attempt to create post");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            User currentUser = userService.getUserByUsername(userDetails.getUsername());
            if (currentUser == null) {
                log.error("User not found: {}", userDetails.getUsername());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }

            PostRequest postRequest = new PostRequest();
            postRequest.setTitle(title);
            postRequest.setDescription(description);
            postRequest.setUserId(currentUser.getId());
            postRequest.setMedia(media);
            postRequest.setShapes(objectMapper.readValue(shapesJson, new TypeReference<List<String>>() {}));
            postRequest.setColors(objectMapper.readValue(colorsJson, new TypeReference<List<String>>() {}));
            postRequest.setMaterials(objectMapper.readValue(materialsJson, new TypeReference<List<String>>() {}));
            Set<WikidataLabelRequest> labels = objectMapper.readValue(wikiDataLabelsJson, 
                new TypeReference<Set<WikidataLabelRequest>>() {});
            
            log.info("Received WikiData labels: {}", labels);
            
            labels.forEach(label -> {
                log.info("Label QID: {}, Title: {}", label.getQid(), label.getTitle());
            });
            
            labels = labels.stream()
                .filter(label -> label.getQid() != null && !label.getQid().isEmpty())
                .collect(Collectors.toSet());
            
            postRequest.setWikiDataLabels(labels);
            postRequest.setWeight(weight);
            postRequest.setHeight(height);
            postRequest.setWidth(width);
            postRequest.setDepth(depth);
            postRequest.setTags(objectMapper.readValue(tagsJson, new TypeReference<Set<Tag>>() {}));
            log.info("Created PostRequest: {}", postRequest);

            Post post = postService.createPost(postRequest, media);
            log.info("Successfully created post: {}", post.getId());
            
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            log.error("Error creating post: ", e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Failed to create post: " + e.getMessage()
            );
        }
    }

    @GetMapping("/main")
    public ResponseEntity<PageResponse<Post>> getMainPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Post> posts = postService.getMainPosts(page, size);
            return ResponseEntity.ok(PageResponse.from(posts));
        } catch (Exception e) {
            log.error("Error fetching main posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<Post>> searchPosts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.info("Searching posts with query: {}, page: {}, size: {}", query, page, size);
            Page<Post> postPage = postService.searchPosts(query, page, size);
            return ResponseEntity.ok(PageResponse.from(postPage));
        } catch (Exception e) {
            log.error("Error searching posts: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPostsByUser(@PathVariable Long userId) {
        List<Post> posts = postService.getPostsByUser(userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return ResponseEntity.ok(post);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "media", required = false) MultipartFile media,
            @RequestParam("shapes") String shapesJson,
            @RequestParam("colors") String colorsJson,
            @RequestParam("materials") String materialsJson,
            @RequestParam("wikiDataLabels") String wikiDataLabelsJson,
            @RequestParam("weight") int weight,
            @RequestParam("height") int height,
            @RequestParam("width") int width,
            @RequestParam("depth") int depth,
            @RequestParam("tags") String tagsJson,
            @RequestParam("userId") Long userId
    ) throws IOException {
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle(title);
        postRequest.setDescription(description);
        postRequest.setMedia(media);
        postRequest.setShapes(objectMapper.readValue(shapesJson, new TypeReference<List<String>>() {}));
        postRequest.setColors(objectMapper.readValue(colorsJson, new TypeReference<List<String>>() {}));
        postRequest.setMaterials(objectMapper.readValue(materialsJson, new TypeReference<List<String>>() {}));
        postRequest.setWikiDataLabels(objectMapper.readValue(wikiDataLabelsJson, new TypeReference<Set<WikidataLabelRequest>>() {}));
        postRequest.setWeight(weight);
        postRequest.setHeight(height);
        postRequest.setWidth(width);
        postRequest.setDepth(depth);
        postRequest.setTags(objectMapper.readValue(tagsJson, new TypeReference<Set<Tag>>() {}));
        postRequest.setUserId(userId);

        Post updatedPost = postService.updatePost(id, postRequest);
        return ResponseEntity.ok(updatedPost);
    }

}
