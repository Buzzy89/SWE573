package com.yusuf.mysticalObject.service;

import com.yusuf.mysticalObject.dto.PostRequest;
import com.yusuf.mysticalObject.dto.WikidataLabelRequest;
import com.yusuf.mysticalObject.entity.Post;
import com.yusuf.mysticalObject.entity.Tag;
import com.yusuf.mysticalObject.entity.User;
import com.yusuf.mysticalObject.entity.WikiDataLabel;
import com.yusuf.mysticalObject.repository.PostRepository;
import com.yusuf.mysticalObject.repository.TagRepository;
import com.yusuf.mysticalObject.repository.UserRepository;
import com.yusuf.mysticalObject.repository.WikiDataLabelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Autowired
    private WikiDataLabelRepository wikiDataLabelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoogleCloudStorageService storageService;

    @Autowired
    private TagRepository tagRepository;

    public Page<Post> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAllPosts(pageable);
        // Load user information for each post
        posts.getContent().forEach(post -> {
            userRepository.findById(post.getUser().getId()).ifPresent(user ->
                    post.setUser(user));
        });
        return posts;
    }

    public Post createPost(PostRequest postRequest, MultipartFile media) {
        try {
            Post post = new Post();
            post.setTitle(postRequest.getTitle());
            post.setDescription(postRequest.getDescription());

            // Handle media upload
            if (media != null && !media.isEmpty()) {
                String mediaUrl = storageService.uploadFile(media);
                post.setMediaUrl(mediaUrl);
            }

            // Set other fields
            post.setShapes(postRequest.getShapes());
            post.setColors(postRequest.getColors());
            post.setMaterials(postRequest.getMaterials());
            post.setWeight(postRequest.getWeight());
            post.setHeight(postRequest.getHeight());
            post.setWidth(postRequest.getWidth());
            post.setDepth(postRequest.getDepth());

            // Handle user
            User user = userRepository.findById(postRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            post.setUser(user);

            // Handle tags
            Set<Tag> tags = handleTags(postRequest.getTags());
            post.setTags(tags);

            // Handle WikiData labels
            Set<WikiDataLabel> wikiDataLabels = handleWikiDataLabels(postRequest.getWikiDataLabels());
            post.setWikiDataLabels(wikiDataLabels);

            return postRepository.save(post);
        } catch (Exception e) {
            log.error("Error creating post", e);
            throw new RuntimeException("Failed to create post", e);
        }
    }

    public Optional<Post> getPostById(Long id) {
        Optional<Post> postOpt = postRepository.findById(id);
        postOpt.ifPresent(post -> {
            userRepository.findById(post.getUser().getId()).ifPresent(user ->
                    post.setUser(user));
        });
        return postOpt;
    }

    public List<Post> getPostsByTags(List<String> tags) {
        List<Post> posts = postRepository.findPostsByTags(tags, (long) tags.size());
        posts.forEach(post -> {
            userRepository.findById(post.getUser().getId()).ifPresent(user ->
                    post.setUser(user));
        });
        return posts;
    }

    public List<Post> getPostsByUser(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            posts.forEach(post -> post.setUser(user));
        }
        return posts;
    }

    public Post updatePost(Long id, PostRequest request) throws IOException {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Verify that the user owns this post
        if (!existingPost.getUser().getId().equals(request.getUserId())) {
            throw new RuntimeException("Unauthorized to edit this post");
        }

        // Handle file upload to Google Cloud Storage
        String mediaUrl = existingPost.getMediaUrl(); // Keep existing media if no new media
        if (request.getMedia() != null && !request.getMedia().isEmpty()) {
            // Delete old media if it exists
            if (existingPost.getMediaUrl() != null) {
                storageService.deleteFile(existingPost.getMediaUrl());
            }
            mediaUrl = storageService.uploadFile(request.getMedia());
        }

        // Process WikiData labels
        Set<WikiDataLabel> wikiDataLabels = new HashSet<>();
        if (request.getWikiDataLabels() != null) {
            wikiDataLabels = request.getWikiDataLabels().stream()
                    .<WikiDataLabel>map(labelRequest -> WikiDataLabel.builder()
                            .wikidataId(labelRequest.getQid())
                            .title(labelRequest.getTitle())
                            .description(labelRequest.getDescription())
                            .build())
                    .collect(Collectors.toSet());
            wikiDataLabels = new HashSet<>(wikiDataLabelRepository.saveAll(wikiDataLabels));
        }

        // Update post fields
        existingPost.setTitle(request.getTitle());
        existingPost.setDescription(request.getDescription());
        existingPost.setMediaUrl(mediaUrl);
        existingPost.setShapes(request.getShapes());
        existingPost.setColors(request.getColors());
        existingPost.setMaterials(request.getMaterials());
        existingPost.setWeight(request.getWeight());
        existingPost.setHeight(request.getHeight());
        existingPost.setWidth(request.getWidth());
        existingPost.setDepth(request.getDepth());
        existingPost.setWikiDataLabels(wikiDataLabels);
        existingPost.setTags(request.getTags());

        existingPost = postRepository.save(existingPost);

        // Add user information to the response
        Post finalExistingPost = existingPost;
        userRepository.findById(existingPost.getUser().getId()).ifPresent(user ->
                finalExistingPost.setUser(user));

        return existingPost;
    }

    public Page<Post> searchPosts(String query, int page, int size) {
        try {
            log.info("Service: Searching posts with query: {}", query);
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> posts = postRepository.searchPosts(query, pageable);
            log.info("Found {} posts", posts.getTotalElements());

            // Add user information to each post
            posts.getContent().forEach(post -> {
                userRepository.findById(post.getUser().getId()).ifPresent(user ->
                        post.setUser(user));
            });

            return posts;
        } catch (Exception e) {
            log.error("Error in search service: ", e);
            throw e;
        }
    }

    public Page<Post> getMainPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable);
    }

    private Set<Tag> handleTags(Set<Tag> requestTags) {
        if (requestTags == null || requestTags.isEmpty()) {
            return new HashSet<>();
        }

        return requestTags.stream()
                .map(tag -> {
                    // If tag has an ID, try to find it
                    if (tag.getId() != null) {
                        return tagRepository.findById(tag.getId())
                                .orElseGet(() -> tagRepository.save(Tag.builder()
                                        .name(tag.getName())
                                        .build()));
                    }
                    // If tag has no ID, try to find by name or create new
                    return tagRepository.findByName(tag.getName())
                            .orElseGet(() -> tagRepository.save(Tag.builder()
                                    .name(tag.getName())
                                    .build()));
                })
                .collect(Collectors.toSet());
    }

    private Set<WikiDataLabel> handleWikiDataLabels(Set<WikidataLabelRequest> labelRequests) {
        log.info("Converting WikiData labels: {}", labelRequests);
        
        return labelRequests.stream()
            .map(labelRequest -> {
                log.info("Processing label request: {}", labelRequest);
                
                // Önce var olan label'ı kontrol et
                Optional<WikiDataLabel> existingLabel = wikiDataLabelRepository.findByQid(labelRequest.getQid());
                
                if (existingLabel.isPresent()) {
                    return existingLabel.get();
                }
                
                // Yeni label oluştur ve kaydet
                WikiDataLabel newLabel = WikiDataLabel.builder()
                    .qid(labelRequest.getQid())
                    .title(labelRequest.getTitle())
                    .description(labelRequest.getDescription())
                    .build();
                    
                log.info("Saving new WikiDataLabel: {}", newLabel);
                return wikiDataLabelRepository.save(newLabel);  // Önce kaydet
            })
            .collect(Collectors.toSet());
    }
}
