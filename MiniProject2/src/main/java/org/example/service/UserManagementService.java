package org.example.service;

import org.example.model.response.UserLifeCycleManagementResponse;

import java.util.concurrent.CompletableFuture;

public interface UserManagementService {
    CompletableFuture<UserLifeCycleManagementResponse> createUser(String userName, String password, String email);
    CompletableFuture<UserLifeCycleManagementResponse> updateUserRole(String userName, String role);
    CompletableFuture<UserLifeCycleManagementResponse> getAllUsers();
    CompletableFuture<UserLifeCycleManagementResponse> updateUserPassword(String userName, String oldPassword, String newPassword);
}
