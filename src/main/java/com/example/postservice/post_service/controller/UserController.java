package com.example.postservice.post_service.controller;

import com.example.postservice.post_service.entity.User;
import com.example.postservice.post_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody User updatedUser) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User updatedProfile = userService.updateUserProfile(username, updatedUser);
        return ResponseEntity.ok(updatedProfile);
    }
}