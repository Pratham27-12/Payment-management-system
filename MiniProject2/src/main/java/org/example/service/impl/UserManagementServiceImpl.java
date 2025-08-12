package org.example.service.impl;

import org.example.model.response.UserLifeCycleManagementResponse;
import org.example.model.enums.UserRole;
import org.example.repository.UserRepository;
import org.example.model.dto.User;
import org.example.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.example.util.PasswordUtil.checkPassword;
import static org.example.util.PasswordUtil.hashPassword;

@Service
public class UserManagementServiceImpl implements UserManagementService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserLifeCycleManagementResponse createUser(User user) {
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
//        Optional<User> existingUser = userRepository.getUserByUserName(user.getUsername());
//        if (existingUser.isPresent()) {
//            return createUserResponse("User already exists", "FAILURE");
//        }

        userRepository.save(user);
        return createUserResponse("User Created Successfully", "SUCCESS");
    }

    @Override
    public UserLifeCycleManagementResponse updateUserRole(String userToUpdate, String role) {

        int updated = userRepository.updateUserRole(userToUpdate, UserRole.valueOf(role));
        if (updated == 1) {
            return createUserResponse("User Role Updated Successfully", "SUCCESS");
        }

        return createUserResponse("Failed to update user role", "FAILURE");
    }

    @Override
    public UserLifeCycleManagementResponse getAllUsers() {
        List<User> users =  userRepository.findAll();
        if(users.isEmpty())
            return UserLifeCycleManagementResponse.builder().status("SUCCESS").message("No users found").build();
        return UserLifeCycleManagementResponse.builder().status("SUCCESS").users(users).build();
    }

    @Override
    public UserLifeCycleManagementResponse updateUserPassword(String userName, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.getUserByUserName(userName);
        if (!userOpt.isPresent()) {
            return createUserResponse("User not found", "FAILURE");
        }
        User user = userOpt.get();
        if (!checkPassword(oldPassword, user.getPassword())) {
            return createUserResponse("Old password is incorrect", "FAILURE");
        }
        String hashedNewPassword = hashPassword(newPassword);
        int updated = userRepository.updateUserPassword(userName, hashedNewPassword);
        if (updated == 1) {
            return createUserResponse("Password updated successfully", "SUCCESS");
        } else {
            return createUserResponse("Failed to update password", "FAILURE");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private UserLifeCycleManagementResponse createUserResponse(String message, String status) {
        return UserLifeCycleManagementResponse.builder()
                .message(message)
                .status(status)
                .build();
    }
}