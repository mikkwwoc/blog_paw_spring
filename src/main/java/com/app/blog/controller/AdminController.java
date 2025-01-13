package com.app.blog.controller;

import com.app.blog.model.User;
import com.app.blog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // Wyświetlenie listy użytkowników
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "adminUsers"; // Przekierowanie do widoku Thymeleaf lub JSP
    }

    // Wyświetlenie formularza do zmiany roli
    @GetMapping("/users/{id}/edit")
    public String editUserRole(@PathVariable Long id, Model model) {
        model.addAttribute("userId", id);
        return "adminUsersEditRole";
    }

    // Obsługa zmiany roli użytkownika
    @PostMapping("/users/{id}/edit")
    public String changeUserRole(@PathVariable Long id, @RequestParam("role") String roleName) {
        userService.changeUserRole(id, roleName);
        return "redirect:/admin/users"; // Powrót do listy użytkowników
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, Model model) {
        try {
            userService.deleteUser(id);
            return "redirect:/adminUsers?elegancko";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "adminUsers";
        }
    }


}