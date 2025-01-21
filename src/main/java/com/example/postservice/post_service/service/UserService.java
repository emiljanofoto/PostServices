package com.example.postservice.post_service.service;

import com.example.postservice.post_service.entity.User;
import com.example.postservice.post_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditService auditService;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUserProfile(String username, User updatedUser) {
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update email
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }

        // Update password
        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        User savedUser = userRepository.save(existingUser);
        auditService.logAction("PROFILE_UPDATE", username, "Updated profile for user: " + username);
        return savedUser;
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash the password
        User savedUser = userRepository.save(user);
        auditService.logAction("USER_REGISTER", "System", "Registered new user: " + user.getUsername());
        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        User savedUser = userRepository.save(user);
        auditService.logAction("USER_CREATE", "Admin", "Created user: " + user.getUsername());
        return savedUser;
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());

        User savedUser = userRepository.save(existingUser);
        auditService.logAction("USER_UPDATE", "Admin", "Updated user with ID: " + id);
        return savedUser;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        auditService.logAction("USER_DELETE", "Admin", "Deleted user with ID: " + id);
    }

    public void resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword)); // Encrypt new password
        userRepository.save(user);
        auditService.logAction("PASSWORD_RESET", "Admin", "Reset password for user with ID: " + id);
    }
}