package com.app.blog.controller;

import com.app.blog.model.*;
import com.app.blog.repository.*;
import com.app.blog.service.CommentService;
import com.app.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


@Controller
public class PostWebController {

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


    @GetMapping("/posts/new")
    public String showForm(Model model) {
        model.addAttribute("post", new Post());
        return "addPost";
    }

    @PostMapping("/addPost")
    public String submitForm(@ModelAttribute Post post,
                             @RequestParam("image") MultipartFile image,
                             RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        post.setUser(user);

        if (!image.isEmpty()) {
            try {
                String fileName = Instant.now().toEpochMilli() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                Files.copy(image.getInputStream(), filePath);
                post.setImagePath(fileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Nie udało się przesłać zdjęcia.");
                return "redirect:/posts/new";
            }
        }

        postRepository.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/posts")
    public String showPosts(Model model) {
        model.addAttribute("posts", postRepository.findAll(Sort.by(Sort.Order.desc("createdAt"))));
        return "posts";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    @GetMapping("/posts/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("post", postRepository.findById(id).get());
        return "editPost";
    }

    @PostMapping("/updatePost")
    public String updatePost(@ModelAttribute Post post) {
        postService.updatePost(post.getId(), post);
        return "redirect:/posts";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}")
    public String showPost(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        Post post = postRepository.findById(id).orElseThrow();
        int likeCount = postService.getLikeCount(post.getId());
        int dislikeCount = postService.getDislikeCount(post.getId());


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Page<Comment> commentPage = commentService.getPaginatedCommentsByPostId(id, page, size);

        User user = userRepository.findByUsername(username);

        boolean likedByUser = postLikeRepository.existsByUserAndPost(user, post);
        boolean dislikedByUser = postDislikeRepository.existsByUserAndPost(user, post);

        if (commentPage.isEmpty()) {
            model.addAttribute("noComments", true);
        }

        model.addAttribute("comments", commentPage.getContent());
        model.addAttribute("currentPage", commentPage.getNumber());
        model.addAttribute("totalPages", commentPage.getTotalPages());
        model.addAttribute("pageSize", commentPage.getSize());
        model.addAttribute("post", post);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likedByUser", likedByUser);
        model.addAttribute("dislikedByUser", dislikedByUser);
        model.addAttribute("dislikeCount", dislikeCount);
        model.addAttribute("comment", new Comment());

        return "showPost";
    }

    @PostMapping("/posts/{id}/comments")
    public String addComment(@PathVariable Long id, @ModelAttribute Comment comment) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono posta z id: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username);
        comment.setUser(user);
        comment.setPost(post);

        comment.setId(null);
        comment.setPost(post);
        commentRepository.save(comment);

        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/like")
    public String toggleLike(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono posta z id: " + id));
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (postDislikeRepository.existsByUserAndPost(user, post)) {
            postService.toggleDislike(user, id);
            postService.toggleLike(user, id);
        }else{
            postService.toggleLike(user, id);
        }

        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/dislike")
    public String toggleDislike(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono posta z id: " + id));
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (postLikeRepository.existsByUserAndPost(user, post)) {
            postService.toggleLike(user, id);
            postService.toggleDislike(user, id);
        }else {
            postService.toggleDislike(user, id);
        }
        return "redirect:/posts/" + id;
    }
    @GetMapping("/api/posts/sorted")
    @ResponseBody
    public List<Map<String, Object>> getSortedPosts(@RequestParam String sortBy) {

        List<Post> posts;
        switch (sortBy) {
            case "newest":
                posts = postRepository.findAllByOrderByCreatedAtDesc();
                break;
            case "oldest":
                posts = postRepository.findAllByOrderByCreatedAtAsc();
                break;
            case "mostLiked":
                posts = postRepository.findAllByOrderByLikesDesc();
                break;
            case "mostDisliked":
                posts = postRepository.findAllByOrderByDislikesDesc();
                break;
            default:
                throw new IllegalArgumentException("Invalid sort parameter: " + sortBy);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication != null && authentication.isAuthenticated()
                ? authentication.getName()
                : null;


        return posts.stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            map.put("createdAt", post.getCreatedAt());
            map.put("authorName", post.getUser() != null ? post.getUser().getUsername() : "NIEZNANY UŻYTKOWNIK");
            map.put("userId", post.getUser() != null ? post.getUser().getId() : null);
            map.put("imagePath", post.getImagePath());
            map.put("likeCount", postLikeRepository.countByPost(post));
            map.put("dislikeCount", postDislikeRepository.countByPost(post));

            boolean isEditable = false;
            boolean isDeletable = false;

            if (authentication != null && authentication.isAuthenticated()) {
                boolean isAdminOrModerator = hasAnyRole(authentication, "ADMIN", "MODERATOR");

                boolean isOwner = post.getUser() != null && post.getUser().getUsername().equals(currentUsername);

                isEditable = isAdminOrModerator || isOwner;
                isDeletable = isEditable;
            }
            map.put("isEditable", isEditable);
            map.put("isDeletable", isDeletable);

            return map;
        }).toList();

    }
    private boolean hasAnyRole(Authentication authentication, String... roles) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        for (String role : roles) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role))) {
                return true;
            }
        }
        return false;
    }

    @GetMapping("/users/{userId}/posts")
    public String getPostsByUser(@PathVariable Long userId, Model model) {
        List<Post> posts = postService.getPostsByUser(userId);
        model.addAttribute("posts", posts);
        model.addAttribute("userId", userId);
        return "userPosts";
    }

}