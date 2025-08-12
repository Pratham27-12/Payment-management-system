package org.example.controller;

import org.example.model.dto.User;
import org.example.model.request.PasswordChangeRequest;
import org.example.model.response.UserLifeCycleManagementResponse;
import org.example.service.UserManagementService;
import org.example.service.impl.UserManagementServiceImpl;
import org.example.util.DeferredResultUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import static org.example.model.route.PaymentRoute.API;
import static org.example.model.route.PaymentRoute.UPDATE_USER_PASSWORD;
import static org.example.model.route.PaymentRoute.USERS;
import static org.example.model.route.PaymentRoute.V1;

@RestController
@RequestMapping(API + V1)
public class UserController {

    private final UserManagementService userManagementService;

    public UserController(UserManagementServiceImpl userManagementServiceImpl) {
        this.userManagementService = userManagementServiceImpl;
    }

    @PostMapping(USERS)
    public DeferredResult<ResponseEntity<UserLifeCycleManagementResponse>> createUser(@RequestBody User user) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(userManagementService.createUser(user));
    }

    @GetMapping(USERS)
    @PreAuthorize("hasRole('ADMIN')")
    public DeferredResult<ResponseEntity<UserLifeCycleManagementResponse>> getAllUser() {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                userManagementService.getAllUsers());
    }

    @PutMapping(USERS + UPDATE_USER_PASSWORD)
    public DeferredResult<ResponseEntity<UserLifeCycleManagementResponse>> updateUserPassword(
            @PathVariable("userName") String userName,
            @RequestBody PasswordChangeRequest passwordChangeRequest) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                userManagementService.updateUserPassword(userName, passwordChangeRequest.getOldPassword(),
                        passwordChangeRequest.getNewPassword()));
    }
}
