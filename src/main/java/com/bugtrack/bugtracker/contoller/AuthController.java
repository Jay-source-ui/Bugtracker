
package com.bugtrack.bugtracker.contoller;

import com.bugtrack.bugtracker.model.User;
import com.bugtrack.bugtracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    // Show login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
    
    // Show registration page
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/saveUser")
    public String registerUser(@ModelAttribute User user, 
                               @RequestParam String role,
                               Model model) {
        boolean registered = userService.registerUser(user);
        
        if (registered) {
            return "redirect:/login?success";
        } else {
            model.addAttribute("error", "Username or email already exists!");
            return "register";
        }
    }
}