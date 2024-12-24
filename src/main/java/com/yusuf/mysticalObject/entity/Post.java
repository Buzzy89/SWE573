package com.yusuf.mysticalObject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String mediaUrl;

    @ElementCollection
    @Builder.Default
    private List<String> shapes = new ArrayList<>();

    @ElementCollection
    @Builder.Default
    private List<String> colors = new ArrayList<>();

    @ElementCollection
    @Builder.Default
    private List<String> materials = new ArrayList<>();

    @ManyToMany
    @Builder.Default
    private Set<WikiDataLabel> wikiDataLabels = new HashSet<>();

    private int weight;
    private int height;
    private int width;
    private int depth;

    @ManyToMany
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Comment> comments = new HashSet<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public User getUser() {
        return author;
    }

    public void setUser(User user) {
        this.author = user;
    }
}
