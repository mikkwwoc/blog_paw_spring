package com.app.blog.controller;

import com.app.blog.model.Post;
import com.app.blog.model.User;
import com.app.blog.repository.*;
import com.app.blog.service.CommentService;
import com.app.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;


    @Value("${upload.path}")
    private String uploadDir;
    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private PostDislikeRepository postDislikeRepository;


    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono posta"));


        if (postLikeRepository.existsByUserAndPost(user, post)) {
            postService.toggleLike(user, id);
            postService.toggleDislike(user, id);
        } else {
            postService.toggleDislike(user, id);
        }
        int likeCount = postService.getLikeCount(post.getId());
        int dislikeCount = postService.getDislikeCount(post.getId());
        boolean likedByUser = postLikeRepository.existsByUserAndPost(user, post);
        boolean dislikedByUser = postDislikeRepository.existsByUserAndPost(user, post);

        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", likeCount);
        response.put("dislikeCount", dislikeCount);
        response.put("likedByUser", likedByUser);
        response.put("dislikedByUser", dislikedByUser);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/dislike")
    public ResponseEntity<Map<String, Object>> toggleDislike(@PathVariable Long id, Authentication authentication) {

        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono posta"));

        if (postLikeRepository.existsByUserAndPost(user, post)) {
            postService.toggleLike(user, id);
            postService.toggleDislike(user, id);
        } else {
            postService.toggleDislike(user, id);
        }

        int likeCount = postService.getLikeCount(post.getId());
        int dislikeCount = postService.getDislikeCount(post.getId());
        boolean likedByUser = postLikeRepository.existsByUserAndPost(user, post);
        boolean dislikedByUser = postDislikeRepository.existsByUserAndPost(user, post);

        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", likeCount);
        response.put("dislikeCount", dislikeCount);
        response.put("likedByUser", likedByUser);
        response.put("dislikedByUser", dislikedByUser);

        return ResponseEntity.ok(response);
    }


    @GetMapping()

    public List<Post> getAllPersons() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postService.getPostById(id).orElseThrow();
    }

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.savePost(post);
    }

    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        return postService.updatePost(id, postDetails);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }


}