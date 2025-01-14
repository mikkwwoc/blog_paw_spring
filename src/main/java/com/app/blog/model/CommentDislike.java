package com.app.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "comment_dislikes", schema = "blog", indexes = {
        @Index(name = "user_id", columnList = "user_id"),
        @Index(name = "comment_id", columnList = "comment_id")
})
public class CommentDislike {
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
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

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

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Instant getDislikedAt() {
        return dislikedAt;
    }

    public void setDislikedAt(Instant dislikedAt) {
        this.dislikedAt = dislikedAt;
    }

}