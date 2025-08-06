package org.example.repository.jdbc.impl;

import org.example.model.enums.UserRole;
import org.example.repository.UserRepository;
import org.example.repository.jdbc.constants.UserDetailsQueryConstant;
import org.example.repository.jdbc.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.example.repository.jdbc.constants.UserDetailsQueryConstant.PASSWORD;
import static org.example.repository.jdbc.constants.UserDetailsQueryConstant.USER_ID;
import static org.example.repository.jdbc.constants.UserDetailsQueryConstant.USER_NAME;
import static org.example.repository.jdbc.constants.UserDetailsQueryConstant.USER_ROLE;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return User.builder()
                    .id(rs.getString(USER_ID))
                    .username(rs.getString(USER_NAME))
                    .role(rs.getString(USER_ROLE) != null ? UserRole.valueOf(rs.getString(USER_ROLE)) : null)
                    .password(rs.getString(PASSWORD))
                    .build();
        }
    };

    @Override
    public CompletableFuture<List<User>> getAllUsers() {
        try {
            List<User> users = jdbcTemplate.query(
                    UserDetailsQueryConstant.getAllUserDetails(),
                    userRowMapper
            );

            return CompletableFuture.completedFuture(users);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all users", e);
        }
    }

    @Override
    public CompletableFuture<User> getUserByUserName(String userName) {
        try {
            List<User> users = jdbcTemplate.query(
                    UserDetailsQueryConstant.getUserDetailsByUserName(),
                    userRowMapper,
                    userName
            );

            User user = users.isEmpty() ? null : users.get(0);
            return CompletableFuture.completedFuture(user);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user by username: " + userName, e);
        }
    }

    @Override
    public CompletableFuture<Void> createUser(User user) {
        try {
            jdbcTemplate.update(
                    UserDetailsQueryConstant.createUserDetails(),
                    user.getId(),
                    user.getUsername(),
                    user.getRole() != null ? user.getRole().name() : null,
                    user.getPassword()
            );
            return CompletableFuture.completedFuture(null);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("User with UserName Already Exist!");
        } catch (Exception e) {
            throw new RuntimeException("Internal Server Error");
        }
    }

    @Override
    public CompletableFuture<Void> updateUserRole(String userName, UserRole role) {
        try {
            int rowsAffected = jdbcTemplate.update(
                    UserDetailsQueryConstant.updateUserRoleByUserName(),
                    role != null ? role.name() : null,
                    userName
            );

            if (rowsAffected == 0) {
                throw new RuntimeException("No user found with username: " + userName);
            }

            return CompletableFuture.completedFuture(null);
        } catch (RuntimeException throwable) {
            throw throwable;
        } catch (Exception e) {
            throw new RuntimeException("Error updating user role for username: " + userName, e);
        }
    }

    @Override
    public CompletableFuture<Void> updateUserPassword(String userName, String password) {
        try {
           jdbcTemplate.update(
                    UserDetailsQueryConstant.updateUserPasswordByUserName(),
                    password,
                    userName
            );

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            throw new RuntimeException("Error updating user role for username: " + userName, e);
        }
    }
}
