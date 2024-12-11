package com.app.blog.controller;

import com.app.blog.model.User;
import com.app.blog.model.Role;
import com.app.blog.repository.UserRepository;
import com.app.blog.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    //logger
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user) {
        logger.info("Rejestracja nowego użytkownika: {}", user.getUsername());

        Role role = roleRepository.findByName("user");
        if (role == null) {
            logger.error("Brak roli 'user' w bazie danych!");
            return "redirect:/error"; // Zwróć użytkownika na stronę błędu
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);

        logger.info("Zapisuję użytkownika o nazwie: {}", user.getUsername());
        userRepository.save(user);

        logger.info("Rejestracja użytkownika {} zakończona sukcesem.", user.getUsername());

        return "redirect:/login";
    }
}