package com.sms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * User entity — maps to the `users` table in the database.
 * Represents an authenticated user with a specific role (ADMIN, TEACHER, STUDENT).
 * Passwords are stored as BCrypt hashes, never plain text.
 */
@Entity
@Table(name = "users")
public class User {

    // Primary key — auto-incremented by the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    // Username must be unique and between 3–50 characters
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    // Stores the BCrypt-hashed password (never raw password)
    @NotBlank(message = "Password is required")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    // Role determines access level: ADMIN, TEACHER, or STUDENT
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    /**
     * Enum for user roles — must match the ENUM defined in the database.
     */
    public enum Role {
        ADMIN, TEACHER, STUDENT
    }

    // ------- Constructors -------

    public User() {}

    public User(String username, String passwordHash, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // ------- Getters and Setters -------

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", username='" + username + "', role=" + role + "}";
    }
}
