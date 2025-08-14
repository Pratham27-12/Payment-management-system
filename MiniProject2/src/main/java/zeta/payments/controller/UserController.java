package zeta.payments.controller;

import zeta.payments.entity.User;
import zeta.payments.dto.request.PasswordChangeRequest;
import zeta.payments.dto.request.RoleChangeRequest;
import zeta.payments.dto.response.UserLifeCycleManagementResponse;
import zeta.payments.service.UserManagementService;
import zeta.payments.service.impl.UserManagementServiceImpl;
import zeta.payments.util.ResponseEntityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

import static zeta.payments.commons.route.PaymentRoute.API;
import static zeta.payments.commons.route.PaymentRoute.UPDATE_USER_PASSWORD;
import static zeta.payments.commons.route.PaymentRoute.USERS;
import static zeta.payments.commons.route.PaymentRoute.V1;

@RestController
@RequestMapping(API + V1)
public class UserController {

    Logger logger = Logger.getLogger(UserController.class.getName());

    private final UserManagementService userManagementService;

    public UserController(UserManagementServiceImpl userManagementServiceImpl) {
        this.userManagementService = userManagementServiceImpl;
    }

    @PostMapping(USERS)
    public ResponseEntity<UserLifeCycleManagementResponse> createUser(@RequestBody User user) {
        logger.info("Creating user: " + user.getUsername());
        return ResponseEntityUtil.getResultWithResponseEntity(userManagementService.createUser(user));
    }

    @GetMapping(USERS)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserLifeCycleManagementResponse> getAllUser() {
        logger.info("Fetching all users");
        return ResponseEntityUtil.getResultWithResponseEntity(userManagementService.getAllUsers());
    }

    @PutMapping(USERS + UPDATE_USER_PASSWORD)
    public ResponseEntity<UserLifeCycleManagementResponse> updateUserPassword(
            @PathVariable("userName") String userName,
            @RequestBody PasswordChangeRequest passwordChangeRequest) {
        logger.info("Updating password for user: " + userName);
        return ResponseEntityUtil.getResultWithResponseEntity(userManagementService.updateUserPassword(userName,
                passwordChangeRequest.getOldPassword(), passwordChangeRequest.getNewPassword()));
    }

    @PutMapping(USERS + "/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserLifeCycleManagementResponse> updateUserRole(
            @RequestBody RoleChangeRequest userToUpdate){
        logger.info("Updating role for user: " + userToUpdate.getUserName());
        return ResponseEntityUtil.getResultWithResponseEntity(
                userManagementService.updateUserRole(userToUpdate.getUserName(), userToUpdate.getRole()));
    }
}
