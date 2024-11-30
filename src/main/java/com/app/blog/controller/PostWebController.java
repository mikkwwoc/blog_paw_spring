package com.app.blog.controller;

import com.app.blog.model.Comment;
import com.app.blog.repository.CommentRepository;
import com.app.blog.service.CommentService;
import com.app.blog.service.PostService;
import com.app.blog.model.Post;
import com.app.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;



@Controller
public class PostWebController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/posts/new")
    public String showForm(Model model) {
        model.addAttribute("post", new Post());
        return "addPost";
    }

    @PostMapping("/addPost")
    public String submitForm(@ModelAttribute Post post) {
        postRepository.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/posts")
    public String showPosts(Model model) {
        model.addAttribute("posts", postRepository.findAll());
        return "posts";
    }

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

    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}")
    public String showPost(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id).orElseThrow();
        model.addAttribute("post", post);
        model.addAttribute("comment", new Comment());

        return "showPost";
    }

    @PostMapping("/posts/{id}/comments")
    public String addComment(@PathVariable Long id, @ModelAttribute Comment comment) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono posta z id: " + id));

        comment.setId(null);
        comment.setPost(post);
        commentRepository.save(comment);

        return "redirect:/posts/" + id;
    }
}