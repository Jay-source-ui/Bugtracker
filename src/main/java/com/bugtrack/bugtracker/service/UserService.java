package com.bugtrack.bugtracker.service;

import com.bugtrack.bugtracker.model.User;
import com.bugtrack.bugtracker.repository.UserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // New method: Register user with selected role
    public boolean registerUser(User user, String selectedRole) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            return false; // Username already taken
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            return false; // Email already registered
        }
        
        // Encode password (encrypt for security)
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set role based on user selection
        if (selectedRole != null && !selectedRole.isEmpty()) {
            user.setRole(selectedRole);
        } else {
            user.setRole("TESTER"); // Default role if nothing selected
        }
        
        // Set active status
        user.setActive(true);
        
        // Save user to database
        userRepository.save(user);
        return true; // Registration successful
    }
    
    // Old method for backward compatibility (defaults to DEVELOPER)
    public boolean registerUser(User user) {
        return registerUser(user, "DEVELOPER");
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    public List<User> getAllDevelopers() {
        return userRepository.findByRole(User.ROLE_DEVELOPER);
    }

    public List<User> getAllTesters() {
        return userRepository.findByRole(User.ROLE_TESTER);
    }

    public List<User> findAllDevelopersAndTesters() {
        return userRepository.findAllDevelopersAndTesters();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}