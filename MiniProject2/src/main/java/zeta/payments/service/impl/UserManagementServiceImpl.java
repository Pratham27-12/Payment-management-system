package zeta.payments.service.impl;

import zeta.payments.exception.PaymentManagementException;
import zeta.payments.dto.response.UserLifeCycleManagementResponse;
import zeta.payments.commons.enums.UserRole;
import zeta.payments.repository.UserRepository;
import zeta.payments.entity.User;
import zeta.payments.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static zeta.payments.util.PasswordUtil.checkPassword;
import static zeta.payments.util.PasswordUtil.hashPassword;

@Service
public class UserManagementServiceImpl implements UserManagementService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserLifeCycleManagementResponse createUser(User user) {
        try {
            String hashedPassword = hashPassword(user.getPassword());
            user.setPassword(hashedPassword);
            Optional<User> existingUser = userRepository.getUserByUserName(user.getUsername());

            if (existingUser.isPresent())
                throw new PaymentManagementException(409, "User already exists with username: " + user.getUsername(), "FAILURE");

            userRepository.save(user);
            return createUserResponse("User Created Successfully",List.of(user), "SUCCESS");

        } catch (PaymentManagementException e) {
           throw new PaymentManagementException(e.getHttpStatus(), e.getMessage(), e.getStatus());
        } catch (Exception e) {
            throw new PaymentManagementException(500, "An error occurred while creating user: " + e.getMessage(), "FAILURE");
        }
    }

    @Override
    public UserLifeCycleManagementResponse updateUserRole(String userToUpdate, UserRole role) {

        try {
            int updated = userRepository.updateUserRole(userToUpdate, role);
            if (updated == 1) {
                return UserLifeCycleManagementResponse.builder().message("User Role Updated Successfully").status("SUCCESS").build();
            }
            throw new PaymentManagementException(404, "User not found or role update failed", "FAILURE");
        } catch (PaymentManagementException e) {
            throw  new PaymentManagementException(e.getHttpStatus(), e.getMessage(), e.getStatus());
        } catch (Exception e) {
            throw new PaymentManagementException(500, "Internal Server Error", "FAILURE");
        }
    }

    @Override
    public UserLifeCycleManagementResponse getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return createUserResponse("Users fetched successfully", users, "SUCCESS");
        } catch (Exception e) {
            throw new PaymentManagementException(500, "An error occurred while fetching users", "FAILURE");
        }
    }

    @Override
    public UserLifeCycleManagementResponse updateUserPassword(String userName, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.getUserByUserName(userName);
        if (!userOpt.isPresent()) {
            throw new PaymentManagementException(404, "User not found with username: " + userName, "FAILURE");
        }
        User user = userOpt.get();
        if (!checkPassword(oldPassword, user.getPassword())) {
            throw new PaymentManagementException(400, "Password is incorrect", "FAILURE");
        }
        String hashedNewPassword = hashPassword(newPassword);
        int updated = userRepository.updateUserPassword(userName, hashedNewPassword);
        if (updated == 1) {
            return createUserResponse("Password updated successfully",List.of(user), "SUCCESS");
        } else {
            throw new PaymentManagementException(500, "Internal Server Error", "FAILURE");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private UserLifeCycleManagementResponse createUserResponse(String message,List<User> users, String status) {
        return UserLifeCycleManagementResponse.builder()
                .message(message)
                .users(users)
                .status(status)
                .build();
    }
}