package org.example.controller;

import org.example.model.request.LoginRequest;
import org.example.model.request.PasswordChangeRequest;
import org.example.model.response.UserLifeCycleManagementResponse;
import org.example.repository.jdbc.dao.User;
import org.example.service.UserManagementService;
import org.example.service.impl.UserManagementServiceImpl;
import org.example.util.DeferredResultUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserManagementService userManagementService;

    public UserController(UserManagementServiceImpl userManagementServiceImpl) {
        this.userManagementService = userManagementServiceImpl;
    }

    @PostMapping("/users/create")
    public DeferredResult<ResponseEntity<UserLifeCycleManagementResponse>> createUser(@RequestBody LoginRequest user) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(userManagementService.createUser(user.getUsername(), user.getPassword()));
    }

    @PostMapping("/users/update-role")
    @PreAuthorize("hasRole('ADMIN')")
    public DeferredResult<ResponseEntity<UserLifeCycleManagementResponse>> updateUserRole(@RequestBody User user) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                userManagementService.updateUserRole(user.getUsername(), user.getRole().name()));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public DeferredResult<ResponseEntity<UserLifeCycleManagementResponse>> getAllUser(@RequestBody User user) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                userManagementService.getAllUsers());
    }

    @PostMapping("/users/update-password")
    public DeferredResult<ResponseEntity<UserLifeCycleManagementResponse>> updateUserPassword(
            @RequestBody PasswordChangeRequest passwordChangeRequest) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                userManagementService.updateUserPassword(passwordChangeRequest.getUserName(), passwordChangeRequest.getOldPassword(),
                        passwordChangeRequest.getNewPassword()));
    }
}
