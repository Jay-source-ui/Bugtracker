package com.bugtrack.bugtracker.contoller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {

    @PostMapping("/register")
    public String register() {
        return "User registered";
    }

    @PostMapping("/login")
    public String login() {
        return "User login";
    }
}