package com.hospital.management.controller;

import com.hospital.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
//    @Autowired
//    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
