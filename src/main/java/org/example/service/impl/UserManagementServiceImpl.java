package org.example.service.impl;

import org.example.model.response.UserLifeCycleManagementResponse;
import org.example.model.enums.UserRole;
import org.example.repository.UserRepository;
import org.example.repository.jdbc.dao.User;
import org.example.repository.jdbc.impl.UserRepositoryImpl;
import org.example.service.UserManagementService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.example.util.PasswordUtil.checkPassword;
import static org.example.util.PasswordUtil.hashPassword;

@Service
public class UserManagementServiceImpl implements UserManagementService, UserDetailsService {
    private final UserRepository userRepository;

    public UserManagementServiceImpl(UserRepositoryImpl userRepositoryImpl) {
        this.userRepository = userRepositoryImpl;
    }

    @Override
    public CompletableFuture<UserLifeCycleManagementResponse> createUser(String userName, String password, String email) {
        String userId = UUID.randomUUID().toString();
        String hashedPassword = hashPassword(password);
        User user = User.builder().id(userId).username(userName).password(hashedPassword).email(email).role(UserRole.VIEWER).build();
        return userRepository.getUserByUserName(userName).thenCompose(existingUser -> {
            if (existingUser != null) {
                return createUserResponse("User already exists", "FAILURE");
            }
            return userRepository.createUser(user).thenCompose(aVoid ->
                    createUserResponse("User Created Successfully", "SUCCESS")
            );
        });
    }

    @Override
    public CompletableFuture<UserLifeCycleManagementResponse> updateUserRole(String userName, String role) {

        return userRepository.updateUserRole(userName, UserRole.valueOf(role)).thenCompose(aVoid ->
                createUserResponse("User Role Updated Successfully", "SUCCESS")
        ).exceptionally(throwable -> {
            throw new RuntimeException("Failed to update user role: " + throwable.getMessage());
        });
    }

    @Override
    public CompletableFuture<UserLifeCycleManagementResponse> getAllUsers() {
        return userRepository.getAllUsers().thenApply(users -> {
            if (users.isEmpty()) {
                throw new RuntimeException("No users found");
            }
            return UserLifeCycleManagementResponse.builder().status("SUCCESS").users(users).build();
        });
    }

    @Override
    public CompletableFuture<UserLifeCycleManagementResponse> updateUserPassword(String userName, String oldPassword, String newPassword) {
        return userRepository.getUserByUserName(userName).thenCompose(user -> {
            if (user == null) {
                return createUserResponse("User not found", "FAILURE");
            }
            if (!checkPassword(oldPassword, user.getPassword())) {
                return createUserResponse("Old password is incorrect", "FAILURE");
            }
            String hashedNewPassword = hashPassword(newPassword);
            return userRepository.updateUserPassword(userName, hashedNewPassword).thenCompose(aVoid ->
                    createUserResponse("Password updated successfully", "SUCCESS")
            );
        });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CompletableFuture<User> user = userRepository.getUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user.join();
    }

    private CompletableFuture<UserLifeCycleManagementResponse> createUserResponse(String message, String status) {
        UserLifeCycleManagementResponse response = UserLifeCycleManagementResponse.builder()
                .message(message)
                .status(status)
                .build();
        return CompletableFuture.completedFuture(response);
    }
}