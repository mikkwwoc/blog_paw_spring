package com.app.blog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "post_categories", indexes = {
        @Index(name = "category_id", columnList = "category_id")
})
public class PostCategory {
    @EmbeddedId
    private PostCategoryId id;

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @MapsId("categoryId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public PostCategoryId getId() {
        return id;
    }

    public void setId(PostCategoryId id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

}