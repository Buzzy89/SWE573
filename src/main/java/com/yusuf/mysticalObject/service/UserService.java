package com.yusuf.mysticalObject.service;

import com.yusuf.mysticalObject.dto.RegisterRequest;
import com.yusuf.mysticalObject.dto.UserProfileDTO;
import com.yusuf.mysticalObject.entity.User;
import com.yusuf.mysticalObject.entity.Post;
import com.yusuf.mysticalObject.repository.UserRepository;
import com.yusuf.mysticalObject.repository.PostRepository;
import com.yusuf.mysticalObject.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GoogleCloudStorageService storageService;

    public User register(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .recentPosts(postRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 5)).getContent())
                .recentComments(commentRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 5)).getContent())
                .build();
    }

    public User updateAvatar(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete old avatar if exists
        if (user.getAvatar() != null) {
            storageService.deleteFile(user.getAvatar());
        }

        // Upload new avatar
        String avatarUrl = storageService.uploadFile(file);
        user.setAvatar(avatarUrl);
        
        return userRepository.save(user);
    }

    public List<Post> getPostsByUser(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            posts.forEach(post -> post.setUser(user));
        }
        return posts;
    }
}
