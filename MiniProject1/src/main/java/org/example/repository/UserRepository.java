package org.example.repository;

import org.example.model.enums.UserRole;
import org.example.repository.jdbc.dao.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserRepository {
    CompletableFuture<List<User>> getAllUsers();
    CompletableFuture<User> getUserByUserName(String userName);
    CompletableFuture<Void> createUser(User user);
    CompletableFuture<Void> updateUserRole(String userName, UserRole role);
}
