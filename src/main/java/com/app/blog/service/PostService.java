package com.app.blog.service;

import com.app.blog.model.User;
import com.app.blog.model.Post;
import com.app.blog.model.PostLike;
import com.app.blog.model.PostDislike;
import com.app.blog.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @Autowired
    private CommentRepository commentRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public List<Post> getPostsByUser(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    public List<Post> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return postRepository.findAllByUserId(user.getId());
    }


    @Transactional
    public void deletePost(Long id) {
        postLikeRepository.deleteByPostId(id);
        postDislikeRepository.deleteByPostId(id);
        commentRepository.deleteByPostId(id);
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
    public List<Post> getTop3PostsByLikes() {
        Pageable top3Pageable = PageRequest.of(0, 3); // Pobierz 3 pierwsze wyniki
        Page<Post> top3Posts = postRepository.findTop3ByOrderByLikeCountDesc(top3Pageable);
        return top3Posts.getContent();
    }
    public List<Post> getTop3PostsByDislikes() {
        Pageable top3Pageable = PageRequest.of(0, 3); // Pobierz 3 pierwsze wyniki
        Page<Post> top3Posts = postRepository.findTop3ByOrderByDislikeCountDesc(top3Pageable);
        return top3Posts.getContent();
    }
}
