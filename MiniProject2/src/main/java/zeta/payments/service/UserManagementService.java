package zeta.payments.service;

import zeta.payments.entity.User;
import zeta.payments.commons.enums.UserRole;
import zeta.payments.dto.response.UserLifeCycleManagementResponse;


public interface UserManagementService {
    UserLifeCycleManagementResponse createUser(User user);
    UserLifeCycleManagementResponse updateUserRole(String userToUpdate, UserRole role);
    UserLifeCycleManagementResponse getAllUsers();
    UserLifeCycleManagementResponse updateUserPassword(String userName, String oldPassword, String newPassword);
}
