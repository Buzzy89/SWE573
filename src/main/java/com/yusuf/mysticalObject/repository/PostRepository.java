package com.yusuf.mysticalObject.repository;

import com.yusuf.mysticalObject.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name IN :tags GROUP BY p HAVING COUNT(t) = :tagCount")
    List<Post> findPostsByTags(List<String> tags, Long tagCount);


    List<Post> findByUserId(Long userId);
}

