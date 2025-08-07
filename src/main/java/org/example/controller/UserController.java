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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import static org.example.model.route.PaymentRoute.CREATE;
import static org.example.model.route.PaymentRoute.GET_ALL;
import static org.example.model.route.PaymentRoute.UPDATE_USER_PASSWORD;
import static org.example.model.route.PaymentRoute.UPDATE_USER_ROLE;
import static org.example.model.route.PaymentRoute.USERS_BASE_URL;
import static org.example.model.route.PaymentRoute.USER_NAME;

@RestController
@RequestMapping(USERS_BASE_URL)
public class UserController {

    private final UserManagementService userManagementService;

    public UserController(UserManagementServiceImpl userManagementServiceImpl) {
        this.userManagementService = userManagementServiceImpl;
    }

    @PostMapping(CREATE)
    public DeferredResult<ResponseEntity<UserLifeCycleManagementResponse>> createUser(@RequestBody LoginRequest user) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(userManagementService.createUser(user.getUsername(), user.getPassword()));
    }

    @PostMapping(UPDATE_USER_ROLE)
    @PreAuthorize("hasRole('ADMIN')")
    public DeferredResult<ResponseEntity<UserLifeCycleManagementResponse>> updateUserRole(@RequestBody User user) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                userManagementService.updateUserRole(user.getUsername(), user.getRole().name()));
    }

    @GetMapping(GET_ALL)
    @PreAuthorize("hasRole('ADMIN')")
    public DeferredResult<ResponseEntity<UserLifeCycleManagementResponse>> getAllUser(@RequestBody User user) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                userManagementService.getAllUsers());
    }

    @PostMapping(UPDATE_USER_PASSWORD)
    public DeferredResult<ResponseEntity<UserLifeCycleManagementResponse>> updateUserPassword(
            @PathVariable("userName") String userName,
            @RequestBody PasswordChangeRequest passwordChangeRequest) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                userManagementService.updateUserPassword(userName, passwordChangeRequest.getOldPassword(),
                        passwordChangeRequest.getNewPassword()));
    }
}
