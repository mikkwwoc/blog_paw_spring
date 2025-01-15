package com.app.blog.controller;

import com.app.blog.model.Post;
import com.app.blog.model.User;
import com.app.blog.repository.*;
import com.app.blog.service.CommentService;
import com.app.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @PostMapping("/{id}/like")
    @ResponseBody
    public Map<String, Object> toggleLike(@PathVariable Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono posta z id: " + id));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username);

        if (postDislikeRepository.existsByUserAndPost(user, post)) {
            postService.toggleDislike(user, id);
        }
        postService.toggleLike(user, id);

        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", postService.getLikeCount(id));
        response.put("dislikeCount", postService.getDislikeCount(id));
        response.put("likedByUser", postLikeRepository.existsByUserAndPost(user, post));
        response.put("dislikedByUser", postDislikeRepository.existsByUserAndPost(user, post));
        return response;
    }

    @PostMapping("/{id}/dislike")
    @ResponseBody
    public Map<String, Object> toggleDislike(@PathVariable Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono posta z id: " + id));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username);

        if (postLikeRepository.existsByUserAndPost(user, post)) {
            postService.toggleLike(user, id);
        }
        postService.toggleDislike(user, id);

        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", postService.getLikeCount(id));
        response.put("dislikeCount", postService.getDislikeCount(id));
        response.put("likedByUser", postLikeRepository.existsByUserAndPost(user, post));
        response.put("dislikedByUser", postDislikeRepository.existsByUserAndPost(user, post));
        return response;
    }

}