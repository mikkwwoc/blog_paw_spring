package com.app.blog.service;

import com.app.blog.model.User;
import com.app.blog.model.Post;
import com.app.blog.model.PostLike;
import com.app.blog.model.PostDislike;
import com.app.blog.repository.PostDislikeRepository;
import com.app.blog.repository.PostLikeRepository;
import com.app.blog.repository.PostRepository;
import com.app.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostDislikeRepository postDislikeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postLikeRepository.deleteByPostId(id);

        postRepository.deleteById(id);
    }

    public Post updatePost(Long id, Post postDetails) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        return postRepository.save(post);
    }

    public void toggleLike(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono posta o ID: " + postId));

        Optional<PostLike> existingLike = postLikeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
        } else {
            PostLike postLike = new PostLike();
            postLike.setUser(user);
            postLike.setPost(post);
            postLikeRepository.save(postLike);
        }
    }

    public int getLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono posta o ID: " + postId));
        return postLikeRepository.countByPost(post);
    }

    public void toggleDislike(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono posta o ID: " + postId));

        Optional<PostDislike> existingDislike = postDislikeRepository.findByUserAndPost(user, post);

        if (existingDislike.isPresent()) {
            postDislikeRepository.delete(existingDislike.get());
        } else {
            PostDislike postDislike = new PostDislike();
            postDislike.setUser(user);
            postDislike.setPost(post);
            postDislikeRepository.save(postDislike);
        }
    }

    public int getDislikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono posta o ID: " + postId));
        return postDislikeRepository.countByPost(post);
    }


}
