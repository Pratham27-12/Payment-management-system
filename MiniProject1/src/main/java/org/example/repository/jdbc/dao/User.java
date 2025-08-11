package org.example.repository.jdbc.dao;

import org.example.model.enums.UserRole;

public class User {
    private String id;
    private String username;
    private String password;
    private UserRole role;

    public User() {
    }

    public User(String id, String username, String password, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', username='" + username + "', role=" + role + "}";
    }
}
