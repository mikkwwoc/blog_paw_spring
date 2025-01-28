package com.app.blog.controller;

import com.app.blog.model.Post;
import com.app.blog.model.User;
import com.app.blog.repository.PostDislikeRepository;
import com.app.blog.repository.PostLikeRepository;
import com.app.blog.repository.PostRepository;
import com.app.blog.repository.UserRepository;
import com.app.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostDislikeRepository postDislikeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String homePage(Model model, Authentication authentication) {

        String username = authentication.getName();


        User loggedInUser = userRepository.findByUsername(username);
        List<Post> userPosts = postRepository.findTop3ByUserOrderByCreatedAtDesc(loggedInUser);

        List<Post> topLikedPosts = postService.getTop3PostsByLikes();
        List<Post> topDislikedPosts = postService.getTop3PostsByDislikes();



        model.addAttribute("userPosts", userPosts);
        model.addAttribute("topLikedPosts", topLikedPosts);
        model.addAttribute("topDislikedPosts", topDislikedPosts);

        return "index";
    }

}