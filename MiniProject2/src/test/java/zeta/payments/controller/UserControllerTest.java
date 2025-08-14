package zeta.payments.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import zeta.payments.commons.enums.UserRole;
import zeta.payments.config.SecurityConfig;
import zeta.payments.dto.request.PasswordChangeRequest;
import zeta.payments.dto.request.RoleChangeRequest;
import zeta.payments.dto.response.UserLifeCycleManagementResponse;
import zeta.payments.entity.User;
import zeta.payments.exception.PaymentManagementException;
import zeta.payments.service.impl.UserManagementServiceImpl;
import zeta.payments.util.JwtUtil;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@TestPropertySource(properties = {
        "spring.security.enabled=false"
})
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserManagementServiceImpl userManagementService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserLifeCycleManagementResponse successResponse;
    private PasswordChangeRequest passwordChangeRequest;
    private RoleChangeRequest roleChangeRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.VIEWER);
        testUser.setPassword("hashedPassword");


        successResponse = UserLifeCycleManagementResponse.builder()
                .users(Arrays.asList(testUser))
                .message("Operation successful")
                .status("SUCCESS")
                .build();

        passwordChangeRequest = new PasswordChangeRequest();
        passwordChangeRequest.setOldPassword("oldPassword123");
        passwordChangeRequest.setNewPassword("newPassword456");

        roleChangeRequest = new RoleChangeRequest();
        roleChangeRequest.setUserName("testuser");
        roleChangeRequest.setRole(UserRole.ADMIN);
    }

    @Test
    void createUser_BadRequest_InvalidPayload() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());

        verify(userManagementService, never()).createUser(any(User.class));
    }

    // GET /api/v1/users Tests
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllUser_Success_WithAdminRole() throws Exception {
        when(userManagementService.getAllUsers()).thenReturn(successResponse);

        mockMvc.perform(get("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users[0].id").value(1))
                .andExpect(jsonPath("$.users[0].username").value("testuser"))
                .andExpect(jsonPath("$.users[0].email").value("test@example.com"))
                .andExpect(jsonPath("$.users[0].role").value("VIEWER"));

        verify(userManagementService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getAllUser_Forbidden_WithUserRole() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(userManagementService, never()).getAllUsers();
    }

    @Test
    @WithMockUser(roles = {"FINANCE_MANAGER"})
    void getAllUser_Forbidden_WithFinanceManagerRole() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(userManagementService, never()).getAllUsers();
    }

    @Test
    void getAllUser_Unauthorized_WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(userManagementService, never()).getAllUsers();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllUser_EmptyList() throws Exception {
        UserLifeCycleManagementResponse emptyResponse = UserLifeCycleManagementResponse.builder()
                .users(Collections.emptyList())
                .message("No users found")
                .status("SUCCESS")
                .build();

        when(userManagementService.getAllUsers()).thenReturn(emptyResponse);

        mockMvc.perform(get("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users").isEmpty())
                .andExpect(jsonPath("$.message").value("No users found"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(userManagementService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllUser_ServiceException() throws Exception {
        when(userManagementService.getAllUsers())
                .thenThrow(new PaymentManagementException(500, "Internal Server Error", "FAILURE"));

        mockMvc.perform(get("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(userManagementService, times(1)).getAllUsers();
    }

    // PUT /api/v1/users/updatePassword/{userName} Tests
    @Test
    void updateUserPassword_Success() throws Exception {
        when(userManagementService.updateUserPassword("testuser", "oldPassword123", "newPassword456"))
                .thenReturn(successResponse);

        mockMvc.perform(put("/api/v1/users/testuser/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users[0].username").value("testuser"));

        verify(userManagementService, times(1)).updateUserPassword("testuser", "oldPassword123", "newPassword456");
    }

    @Test
    void updateUserPassword_BadRequest_InvalidPayload() throws Exception {
        mockMvc.perform(put("/api/v1/users/updatePassword/testuser")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isUnauthorized());

        verify(userManagementService, never()).updateUserPassword(anyString(), anyString(), anyString());
    }

    @Test
    void updateUserPassword_UserNotFound() throws Exception {
        when(userManagementService.updateUserPassword("nonexistent", "oldPassword123", "newPassword456"))
                .thenThrow(new PaymentManagementException(404, "User not found", "FAILURE"));

        mockMvc.perform(put("/api/v1/users/nonexistent/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeRequest)))
                .andExpect(status().isNotFound());

        verify(userManagementService, times(1)).updateUserPassword("nonexistent", "oldPassword123", "newPassword456");
    }

    @Test
    void updateUserPassword_InvalidCredentials() throws Exception {
        when(userManagementService.updateUserPassword("testuser", "wrongPassword", "newPassword456"))
                .thenThrow(new PaymentManagementException(401, "Invalid credentials", "FAILURE"));

        PasswordChangeRequest invalidRequest = new PasswordChangeRequest();
        invalidRequest.setOldPassword("wrongPassword");
        invalidRequest.setNewPassword("newPassword456");

        mockMvc.perform(put("/api/v1/users/testuser/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized());

        verify(userManagementService, times(1)).updateUserPassword("testuser", "wrongPassword", "newPassword456");
    }

    @Test
    void updateUserPassword_ServiceException() throws Exception {
        when(userManagementService.updateUserPassword("testuser", "oldPassword123", "newPassword456"))
                .thenThrow(new PaymentManagementException(500, "Internal Server Error", "FAILURE"));

        mockMvc.perform(put("/api/v1/users/testuser/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeRequest)))
                .andExpect(status().isInternalServerError());

        verify(userManagementService, times(1)).updateUserPassword("testuser", "oldPassword123", "newPassword456");
    }

    // PUT /api/v1/users/role Tests
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateUserRole_Success_WithAdminRole() throws Exception {
        when(userManagementService.updateUserRole("testuser", UserRole.ADMIN)).thenReturn(successResponse);

        mockMvc.perform(put("/api/v1/users/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users[0].username").value("testuser"));

        verify(userManagementService, times(1)).updateUserRole("testuser", UserRole.ADMIN);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void updateUserRole_Forbidden_WithUserRole() throws Exception {
        mockMvc.perform(put("/api/v1/users/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleChangeRequest)))
                .andExpect(status().isForbidden());

        verify(userManagementService, never()).updateUserRole(anyString(), any());
    }

    @Test
    @WithMockUser(roles = {"FINANCE_MANAGER"})
    void updateUserRole_Forbidden_WithFinanceManagerRole() throws Exception {
        mockMvc.perform(put("/api/v1/users/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleChangeRequest)))
                .andExpect(status().isForbidden());

        verify(userManagementService, never()).updateUserRole(anyString(), any());
    }

    @Test
    void updateUserRole_Unauthorized_WithoutAuthentication() throws Exception {
        mockMvc.perform(put("/api/v1/users/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleChangeRequest)))
                .andExpect(status().isUnauthorized());

        verify(userManagementService, never()).updateUserRole(anyString(), any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateUserRole_BadRequest_InvalidPayload() throws Exception {
        mockMvc.perform(put("/api/v1/users/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());

        verify(userManagementService, never()).updateUserRole(anyString(), any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateUserRole_UserNotFound() throws Exception {
        when(userManagementService.updateUserRole("nonexistent", UserRole.ADMIN))
                .thenThrow(new PaymentManagementException(404, "User not found", "FAILURE"));

        RoleChangeRequest notFoundRequest = new RoleChangeRequest();
        notFoundRequest.setUserName("nonexistent");
        notFoundRequest.setRole(UserRole.ADMIN);

        mockMvc.perform(put("/api/v1/users/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notFoundRequest)))
                .andExpect(status().isNotFound());

        verify(userManagementService, times(1)).updateUserRole("nonexistent", UserRole.ADMIN);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateUserRole_InvalidRole() throws Exception {
        when(userManagementService.updateUserRole("testuser", UserRole.FINANCE_MANAGER))
                .thenThrow(new PaymentManagementException(400, "Invalid role", "FAILURE"));

        RoleChangeRequest invalidRoleRequest = new RoleChangeRequest();
        invalidRoleRequest.setUserName("testuser");
        invalidRoleRequest.setRole(UserRole.FINANCE_MANAGER);

        mockMvc.perform(put("/api/v1/users/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoleRequest)))
                .andExpect(status().isBadRequest());

        verify(userManagementService, times(1)).updateUserRole("testuser", UserRole.FINANCE_MANAGER);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateUserRole_ServiceException() throws Exception {
        when(userManagementService.updateUserRole("testuser", UserRole.ADMIN))
                .thenThrow(new PaymentManagementException(500, "Internal Server Error", "FAILURE"));

        mockMvc.perform(put("/api/v1/users/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleChangeRequest)))
                .andExpect(status().isInternalServerError());

        verify(userManagementService, times(1)).updateUserRole("testuser", UserRole.ADMIN);
    }

    // Additional edge case tests
    @Test
    void createUser_EmptyRequestBody() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(userManagementService, times(1)).createUser(any(User.class));
    }

    @Test
    @WithMockUser
    void updateUserPassword_EmptyPathVariable() throws Exception {
        mockMvc.perform(put("/api/v1/users/updatePassword")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeRequest)))
                .andExpect(status().isNotFound());

        verify(userManagementService, never()).updateUserPassword(anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllUser_MultipleUsers() throws Exception {
        User user2 = new User();
        user2.setId(2L);
        user2.setUserName("testuser2");
        user2.setEmail("test2@example.com");
        user2.setRole(UserRole.ADMIN);
        user2.setPassword("hashedPassword2");

        UserLifeCycleManagementResponse multipleUsersResponse = UserLifeCycleManagementResponse.builder()
                .users(Arrays.asList(testUser, user2))
                .message("Users fetched successfully")
                .status("SUCCESS")
                .build();

        when(userManagementService.getAllUsers()).thenReturn(multipleUsersResponse);

        mockMvc.perform(get("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Users fetched successfully"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users.length()").value(2))
                .andExpect(jsonPath("$.users[0].username").value("testuser"))
                .andExpect(jsonPath("$.users[0].role").value("VIEWER"))
                .andExpect(jsonPath("$.users[1].username").value("testuser2"))
                .andExpect(jsonPath("$.users[1].role").value("ADMIN"));

        verify(userManagementService, times(1)).getAllUsers();
    }
}