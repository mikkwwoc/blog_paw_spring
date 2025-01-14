package com.app.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "post_dislikes", schema = "blog", indexes = {
        @Index(name = "user_id", columnList = "user_id"),
        @Index(name = "post_id", columnList = "post_id")
})
public class PostDislike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "disliked_at", nullable = false)
    private Instant dislikedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Instant getDislikedAt() {
        return dislikedAt;
    }

    public void setDislikedAt(Instant dislikedAt) {
        this.dislikedAt = dislikedAt;
    }

}