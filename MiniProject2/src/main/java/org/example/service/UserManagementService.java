package org.example.service;

import org.example.model.dto.User;
import org.example.model.response.UserLifeCycleManagementResponse;


public interface UserManagementService {
    UserLifeCycleManagementResponse createUser(User user);
    UserLifeCycleManagementResponse updateUserRole(String userToUpdate, String role);
    UserLifeCycleManagementResponse getAllUsers();
    UserLifeCycleManagementResponse updateUserPassword(String userName, String oldPassword, String newPassword);
}
