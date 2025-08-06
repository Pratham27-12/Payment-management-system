package org.example.util;

import org.example.model.ValidationResponse;
import org.example.model.enums.PaymentStatus;
import org.example.model.enums.UserRole;
import org.example.repository.UserRepository;
import org.example.repository.jdbc.dao.Payment;
import org.example.repository.jdbc.impl.UserRepositoryImpl;
import org.example.service.impl.UserManagementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class ValidatorUtil {

    private static UserRepository userRepository;

    public ValidatorUtil(@Autowired UserRepositoryImpl userRepositoryImpl) {
        userRepository = userRepositoryImpl;
    }

    public static CompletableFuture<ValidationResponse> validatePayment(Payment payment) {
        if (isAmountValid(payment.getAmount()) && isCurrencyValid(payment.getCurrency()) && payment.getStatus().equals(PaymentStatus.PENDING)) {
            return CompletableFuture.completedFuture(new ValidationResponse(true));
        }
        return CompletableFuture.completedFuture(new ValidationResponse("Payment Request Is Invalid"));
    }

    public static CompletableFuture<ValidationResponse> validateUserAdmin(String userName, String password) {
        return userRepository.getUserByUserName(userName)
                .thenCompose(user -> {
                    if (user != null && user.getRole().equals(UserRole.ADMIN)) {
                        return validatePassword(password, user.getPassword());
                    }
                    return CompletableFuture.completedFuture(new ValidationResponse("User is not an admin"));
                });
    }

    public static CompletableFuture<ValidationResponse> validateUserManager(String userName, String password) {
        return userRepository.getUserByUserName(userName)
                .thenCompose(user -> {
                    if (user != null && user.getRole().equals(UserRole.FINANCE_MANAGER)) {
                        return validatePassword(password, user.getPassword());
                    }
                    return CompletableFuture.completedFuture(new ValidationResponse("User is not a finance manager"));
                });
    }

    private static CompletableFuture<ValidationResponse> validatePassword(String password, String hashedPassword) {
        if (password == null || password.isEmpty() || !PasswordUtil.checkPassword(password, hashedPassword)) {
            return CompletableFuture.completedFuture(new ValidationResponse("User or Password Is Invalid"));
        }
        return CompletableFuture.completedFuture(new ValidationResponse(true));
    }

    private static boolean isAmountValid(String amount) {
        try {
            double value = Double.parseDouble(amount);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isCurrencyValid(String curr) {
        return curr != null && curr.matches("[A-Z]{3}");
    }
}
