package org.example.repository.jdbc.impl;

import org.example.model.enums.UserRole;
import org.example.repository.UserRepository;
import org.example.repository.jdbc.constants.PaymentDetailsQueryConstant;
import org.example.repository.jdbc.constants.UserDetailsQueryConstant;
import org.example.repository.jdbc.dao.User;
import org.example.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.example.repository.jdbc.constants.UserDetailsQueryConstant.PASSWORD;
import static org.example.repository.jdbc.constants.UserDetailsQueryConstant.USER_ID;
import static org.example.repository.jdbc.constants.UserDetailsQueryConstant.USER_NAME;
import static org.example.repository.jdbc.constants.UserDetailsQueryConstant.USER_ROLE;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public CompletableFuture<List<User>> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UserDetailsQueryConstant.getAllUserDetails());
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getString(USER_ID));
                user.setUsername(rs.getString(USER_NAME));
                user.setRole(rs.getString(USER_ROLE) != null ? UserRole.valueOf(rs.getString(USER_ROLE)) : null);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching users", e);
        }
        return CompletableFuture.completedFuture(users);
    }

    @Override
    public CompletableFuture<User> getUserByUserName(String userName) {
        User user = null;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UserDetailsQueryConstant.getUserDetailsByUserName())) {

            stmt.setString(1, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getString(USER_ID));
                    user.setUsername(rs.getString(USER_NAME));
                    user.setPassword(rs.getString(PASSWORD));
                    user.setRole(rs.getString(USER_ROLE) != null ? UserRole.valueOf(rs.getString(USER_ROLE)) : null);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by ID", e);
        }
        return CompletableFuture.completedFuture(user);
    }

    @Override
    public CompletableFuture<Void> createUser(User user) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UserDetailsQueryConstant.createUserDetails())) {

            stmt.setString(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getRole() != null ? user.getRole().name() : null);
            stmt.setString(4, user.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating user", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateUserRole(String userName, UserRole role){
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UserDetailsQueryConstant.updateUserRoleByUserName())) {
            stmt.setString(1, role != null ? role.name() : null);
            stmt.setString(2, userName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating User", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    public static void main(String[] args) {
        UserRepository userRepository = new UserRepositoryImpl();
        User newUser = new User();
        newUser.setId("12345");
        newUser.setUsername("john_doe");
        newUser.setRole(UserRole.ADMIN);
        newUser.setPassword("securepassword");
        userRepository.updateUserRole("john_doe", UserRole.FINANCE_MANAGER).whenComplete((result, throwable) -> {
            if (throwable != null) {
                System.err.println("Error creating user: " + throwable.getMessage());
            } else {
                System.out.println("User Updated successfully:");
            }
        });
    }
}
