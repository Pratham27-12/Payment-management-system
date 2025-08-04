package org.example.service.impl;

import org.example.model.UserLifeCycleManagementResponse;
import org.example.model.enums.UserRole;
import org.example.repository.UserRepository;
import org.example.repository.jdbc.dao.User;
import org.example.repository.jdbc.impl.UserRepositoryImpl;
import org.example.service.UserManagementService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.example.util.PasswordUtil.checkPassword;
import static org.example.util.PasswordUtil.hashPassword;
import static org.example.util.ValidatorUtil.validateUserAdmin;

public class UserManagementServiceImpl implements UserManagementService {

    @Override
    public CompletableFuture<UserLifeCycleManagementResponse> createUser(String userName, String password) {
        UserRepository userRepository = new UserRepositoryImpl();
        String userId = UUID.randomUUID().toString();
        String hashedPassword = hashPassword(password);
        User user = new User(userId, userName, hashedPassword, UserRole.VIEWER);
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
    public CompletableFuture<UserLifeCycleManagementResponse> updateUserRole(String userName, String password,
                                                                             String userToUpdate, String role) {
        UserRepository userRepository = new UserRepositoryImpl();
        return validateUserAdmin(userName, password).thenCompose(validationResponse -> {
           if(validationResponse.isValid()){
               return userRepository.updateUserRole(userToUpdate, UserRole.valueOf(role)).thenCompose(aVoid ->
                     createUserResponse("User Role Updated Successfully", "SUCCESS")
               );
           }
           return createUserResponse("Only Admin Can update the Role","FAILURE");
        });
    }

    @Override
    public CompletableFuture<UserRole> verifyUser(String userName, String password){
        UserRepository userRepository = new UserRepositoryImpl();
        return userRepository.getUserByUserName(userName).thenCompose(user ->{
           if(user.getId().isEmpty() || user.getId() == null){
               throw new RuntimeException("No users found");
           }

           if(checkPassword(password,user.getPassword())){
               return CompletableFuture.completedFuture(user.getRole());
           }

            throw new RuntimeException("Password is Incorrect");
        });
    }

    @Override
    public CompletableFuture<List<User>> getAllUsers() {
        UserRepository userRepository = new UserRepositoryImpl();
        return userRepository.getAllUsers().thenApply(users -> {
            if (users.isEmpty()) {
                throw new RuntimeException("No users found");
            }
            return users;
        });
    }

    private CompletableFuture<UserLifeCycleManagementResponse> createUserResponse(String message, String status) {
        UserLifeCycleManagementResponse response = new UserLifeCycleManagementResponse();
        response.setMessage(message);
        response.setStatus(status);
        return CompletableFuture.completedFuture(response);
    }
}
