package org.example.service;

import org.example.model.UserLifeCycleManagementResponse;
import org.example.model.enums.UserRole;
import org.example.repository.jdbc.dao.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserManagementService {
    CompletableFuture<UserLifeCycleManagementResponse> createUser(String userName, String password);
    CompletableFuture<UserLifeCycleManagementResponse> updateUserRole(String userName, String password, String userToUpdate, String role);
    CompletableFuture<List<User>> getAllUsers();
    CompletableFuture<UserRole> verifyUser(String userName, String password);
}
