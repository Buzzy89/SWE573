package com.yusuf.mysticalObject.repository;

import com.yusuf.mysticalObject.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(Long userId);

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    Page<Post> findAllPosts(Pageable pageable);

    @Query(value = "SELECT p.* FROM posts p " +
            "JOIN post_tags pt ON p.id = pt.post_id " +
            "JOIN tags t ON pt.tag_id = t.id " +
            "WHERE t.name IN :tags " +
            "GROUP BY p.id " +
            "HAVING COUNT(DISTINCT t.name) = :tagCount", nativeQuery = true)
    List<Post> findPostsByTags(List<String> tags, Long tagCount);

    Page<Post> findAll(Pageable pageable);

    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query(value = """
        SELECT DISTINCT p.* FROM posts p 
        LEFT JOIN post_tags pt ON p.id = pt.post_id 
        LEFT JOIN tags t ON pt.tag_id = t.id 
        WHERE 
            LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) 
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) 
            OR EXISTS (
                SELECT 1 FROM unnest(p.shapes) shape 
                WHERE LOWER(shape) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            OR EXISTS (
                SELECT 1 FROM unnest(p.colors) color 
                WHERE LOWER(color) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            OR EXISTS (
                SELECT 1 FROM unnest(p.materials) material 
                WHERE LOWER(material) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY p.created_at DESC
        """, 
        countQuery = """
            SELECT COUNT(DISTINCT p.id) FROM posts p 
            LEFT JOIN post_tags pt ON p.id = pt.post_id 
            LEFT JOIN tags t ON pt.tag_id = t.id 
            WHERE 
                LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))
                OR EXISTS (
                    SELECT 1 FROM unnest(p.shapes) shape 
                    WHERE LOWER(shape) LIKE LOWER(CONCAT('%', :query, '%'))
                )
                OR EXISTS (
                    SELECT 1 FROM unnest(p.colors) color 
                    WHERE LOWER(color) LIKE LOWER(CONCAT('%', :query, '%'))
                )
                OR EXISTS (
                    SELECT 1 FROM unnest(p.materials) material 
                    WHERE LOWER(material) LIKE LOWER(CONCAT('%', :query, '%'))
                )
                OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))
            """,
        nativeQuery = true)
    Page<Post> searchPosts(String query, Pageable pageable);

    Page<Post> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String title, String description, Pageable pageable);
}

