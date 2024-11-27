package com.app.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    @GetMapping("/")
    public String get (Model model) {
        model.addAttribute("message", "Blog");
        return "index";
    }
}