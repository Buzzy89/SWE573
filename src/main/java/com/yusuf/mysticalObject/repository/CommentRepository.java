package com.yusuf.mysticalObject.repository;

import com.yusuf.mysticalObject.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_Id(Long postId);
    List<Comment> findByUserId(Long userId);
    Page<Comment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
} 