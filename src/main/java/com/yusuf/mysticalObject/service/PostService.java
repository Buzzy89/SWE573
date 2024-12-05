package com.yusuf.mysticalObject.service;

import com.yusuf.mysticalObject.entity.Post;
import com.yusuf.mysticalObject.entity.Tag;
import com.yusuf.mysticalObject.repository.PostRepository;
import com.yusuf.mysticalObject.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;

    public Post createPost(String title, String description, List<String> tagNames, Long userId) {
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setUserId(userId);

        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName);
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagName);
                tag = tagRepository.save(tag);
            }
            tags.add(tag);
        }
        post.setTags(tags);

        return postRepository.save(post);
    }

    public List<Post> getPostsByTags(List<String> tags) {
        return postRepository.findPostsByTags(tags, (long) tags.size());
    }

    public List<Post> getPostsByUser(Long userId) {
        return postRepository.findByUserId(userId);
    }


}
