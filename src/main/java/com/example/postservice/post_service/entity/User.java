package com.example.postservice.post_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username is mandatory")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Password is mandatory")
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "Role is mandatory")
    private String role; // USER or MANAGER

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email is mandatory")
    private String email;
}
