package com.yusuf.mysticalObject.controller;

import com.yusuf.mysticalObject.dto.PostRequest;
import com.yusuf.mysticalObject.dto.SearchRequest;
import com.yusuf.mysticalObject.entity.Post;
import com.yusuf.mysticalObject.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // Post oluşturma
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody PostRequest postRequest) {
        Post post = postService.createPost(
                postRequest.getTitle(),
                postRequest.getDescription(),
                postRequest.getTags(),
                postRequest.getUser().getId()
        );
        return ResponseEntity.ok(post);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(@RequestBody SearchRequest searchRequest) {
        List<Post> posts = postService.getPostsByTags(searchRequest.getTags());
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPostsByUser(@PathVariable Long userId) {
        List<Post> posts = postService.getPostsByUser(userId);
        return ResponseEntity.ok(posts);
    }

}
