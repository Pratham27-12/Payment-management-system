package zeta.payments.service.impl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import zeta.payments.commons.enums.UserRole;
import zeta.payments.dto.response.UserLifeCycleManagementResponse;
import zeta.payments.entity.User;
import zeta.payments.exception.PaymentManagementException;
import zeta.payments.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserName("testuser");
        testUser.setPassword("plainPassword");
        testUser.setRole(UserRole.VIEWER);
    }

    @Test
    void createUser_Success() {
        when(userRepository.getUserByUserName("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserLifeCycleManagementResponse response = userManagementService.createUser(testUser);

        assertNotNull(response);
        assertEquals("User Created Successfully", response.getMessage());
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(1, response.getUsers().size());
        verify(userRepository).getUserByUserName("testuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_UserAlreadyExists() {
        when(userRepository.getUserByUserName("testuser")).thenReturn(Optional.of(testUser));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> userManagementService.createUser(testUser));

        assertEquals(409, exception.getHttpStatus());
        assertEquals("User already exists with username: testuser", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
        verify(userRepository).getUserByUserName("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_PaymentManagementException() {
        when(userRepository.getUserByUserName("testuser"))
                .thenThrow(new PaymentManagementException(500, "Database error", "FAILURE"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> userManagementService.createUser(testUser));

        assertEquals(500, exception.getHttpStatus());
        assertEquals("Database error", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void createUser_GenericException() {
        when(userRepository.getUserByUserName("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database connection failed"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> userManagementService.createUser(testUser));

        assertEquals(500, exception.getHttpStatus());
        assertTrue(exception.getMessage().contains("An error occurred while creating user"));
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void updateUserRole_Success() {
        when(userRepository.updateUserRole("testuser", UserRole.ADMIN)).thenReturn(1);

        UserLifeCycleManagementResponse response = userManagementService.updateUserRole("testuser", UserRole.ADMIN);

        assertNotNull(response);
        assertEquals("User Role Updated Successfully", response.getMessage());
        assertEquals("SUCCESS", response.getStatus());
        verify(userRepository).updateUserRole("testuser", UserRole.ADMIN);
    }

    @Test
    void updateUserRole_UserNotFound() {
        when(userRepository.updateUserRole("testuser", UserRole.ADMIN)).thenReturn(0);

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> userManagementService.updateUserRole("testuser", UserRole.ADMIN));

        assertEquals(404, exception.getHttpStatus());
        assertEquals("User not found or role update failed", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void updateUserRole_PaymentManagementException() {
        when(userRepository.updateUserRole("testuser", UserRole.ADMIN))
                .thenThrow(new PaymentManagementException(400, "Invalid role", "FAILURE"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> userManagementService.updateUserRole("testuser", UserRole.ADMIN));

        assertEquals(400, exception.getHttpStatus());
        assertEquals("Invalid role", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void updateUserRole_GenericException() {
        when(userRepository.updateUserRole("testuser", UserRole.ADMIN))
                .thenThrow(new RuntimeException("Database error"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> userManagementService.updateUserRole("testuser", UserRole.ADMIN));

        assertEquals(500, exception.getHttpStatus());
        assertEquals("Internal Server Error", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        UserLifeCycleManagementResponse response = userManagementService.getAllUsers();

        assertNotNull(response);
        assertEquals("Users fetched successfully", response.getMessage());
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(1, response.getUsers().size());
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_Exception() {
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> userManagementService.getAllUsers());

        assertEquals(500, exception.getHttpStatus());
        assertEquals("An error occurred while fetching users", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void updateUserPassword_Success() {
        testUser.setPassword("$2a$10$hashedPassword"); // Mock hashed password
        when(userRepository.getUserByUserName("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.updateUserPassword(eq("testuser"), anyString())).thenReturn(1);

        try (var mockPasswordUtil = mockStatic(zeta.payments.util.PasswordUtil.class)) {
            mockPasswordUtil.when(() -> zeta.payments.util.PasswordUtil.checkPassword("oldPassword", "$2a$10$hashedPassword"))
                    .thenReturn(true);
            mockPasswordUtil.when(() -> zeta.payments.util.PasswordUtil.hashPassword("newPassword"))
                    .thenReturn("$2a$10$newHashedPassword");

            UserLifeCycleManagementResponse response = userManagementService.updateUserPassword("testuser", "oldPassword", "newPassword");

            assertNotNull(response);
            assertEquals("Password updated successfully", response.getMessage());
            assertEquals("SUCCESS", response.getStatus());
            assertEquals(1, response.getUsers().size());
        }
    }

    @Test
    void updateUserPassword_UserNotFound() {
        when(userRepository.getUserByUserName("testuser")).thenReturn(Optional.empty());

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> userManagementService.updateUserPassword("testuser", "oldPassword", "newPassword"));

        assertEquals(404, exception.getHttpStatus());
        assertEquals("User not found with username: testuser", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void updateUserPassword_IncorrectOldPassword() {
        testUser.setPassword("$2a$10$hashedPassword");
        when(userRepository.getUserByUserName("testuser")).thenReturn(Optional.of(testUser));

        try (var mockPasswordUtil = mockStatic(zeta.payments.util.PasswordUtil.class)) {
            mockPasswordUtil.when(() -> zeta.payments.util.PasswordUtil.checkPassword("wrongPassword", "$2a$10$hashedPassword"))
                    .thenReturn(false);

            PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                    () -> userManagementService.updateUserPassword("testuser", "wrongPassword", "newPassword"));

            assertEquals(400, exception.getHttpStatus());
            assertEquals("Password is incorrect", exception.getMessage());
            assertEquals("FAILURE", exception.getStatus());
        }
    }

    @Test
    void updateUserPassword_UpdateFailed() {
        testUser.setPassword("$2a$10$hashedPassword");
        when(userRepository.getUserByUserName("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.updateUserPassword(eq("testuser"), anyString())).thenReturn(0);

        try (var mockPasswordUtil = mockStatic(zeta.payments.util.PasswordUtil.class)) {
            mockPasswordUtil.when(() -> zeta.payments.util.PasswordUtil.checkPassword("oldPassword", "$2a$10$hashedPassword"))
                    .thenReturn(true);
            mockPasswordUtil.when(() -> zeta.payments.util.PasswordUtil.hashPassword("newPassword"))
                    .thenReturn("$2a$10$newHashedPassword");

            PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                    () -> userManagementService.updateUserPassword("testuser", "oldPassword", "newPassword"));

            assertEquals(500, exception.getHttpStatus());
            assertEquals("Internal Server Error", exception.getMessage());
            assertEquals("FAILURE", exception.getStatus());
        }
    }

    @Test
    void loadUserByUsername_Success() {
        when(userRepository.getUserByUserName("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userManagementService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        verify(userRepository).getUserByUserName("testuser");
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        when(userRepository.getUserByUserName("testuser")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userManagementService.loadUserByUsername("testuser"));

        assertEquals("User not found: testuser", exception.getMessage());
        verify(userRepository).getUserByUserName("testuser");
    }
}
