package com.yusuf.mysticalObject.service;

import com.yusuf.mysticalObject.dto.CommentRequest;
import com.yusuf.mysticalObject.dto.UserDTO;
import com.yusuf.mysticalObject.entity.Comment;
import com.yusuf.mysticalObject.entity.Post;
import com.yusuf.mysticalObject.entity.User;
import com.yusuf.mysticalObject.repository.CommentRepository;
import com.yusuf.mysticalObject.repository.PostRepository;
import com.yusuf.mysticalObject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Comment createComment(CommentRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment.CommentBuilder commentBuilder = Comment.builder()
                .content(request.getContent())
                .post(post)
                .userId(request.getUserId())
                .replies(new ArrayList<>());

        if (request.getParentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            commentBuilder.parent(parentComment);
        }

        Comment comment = commentBuilder.build();
        comment = commentRepository.save(comment);
        
        addUserToComment(comment);
        
        return comment;
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPost(Long postId) {
        List<Comment> comments = commentRepository.findByPost_Id(postId);
        
        comments.forEach(comment -> {
            addUserToComment(comment);
            addUserInfoToReplies(comment);
        });
        
        return comments.stream()
                .filter(comment -> comment.getParent() == null)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByUser(Long userId) {
        List<Comment> comments = commentRepository.findByUserId(userId);
        comments.forEach(this::addUserToComment);
        return comments;
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private void addUserToComment(Comment comment) {
        userRepository.findById(comment.getUserId()).ifPresent(user -> {
            comment.setUser(mapUserToDTO(user));
        });
    }

    private void addUserInfoToReplies(Comment comment) {
        if (comment.getReplies() != null) {
            comment.getReplies().forEach(reply -> {
                addUserToComment(reply);
                addUserInfoToReplies(reply);
            });
        }
    }

    private UserDTO mapUserToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();
    }
} 