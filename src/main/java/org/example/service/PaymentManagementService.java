package org.example.service;

import org.example.model.PaymentLifeCycleManagementResponse;
import org.example.model.Report;
import org.example.repository.jdbc.dao.Payment;

import java.util.concurrent.CompletableFuture;

public interface PaymentManagementService {
    CompletableFuture<PaymentLifeCycleManagementResponse> createPaymentRecord(Payment payment, String userName, String password);
    CompletableFuture<PaymentLifeCycleManagementResponse> updatePayment(String id, String userName, String password, String status);
    CompletableFuture<Report> generateMonthlyReport(int month, int year);
    CompletableFuture<Report> generateQuarterlyReport(int quarter, int year);
    CompletableFuture<PaymentLifeCycleManagementResponse> getAllPayment();
    CompletableFuture<PaymentLifeCycleManagementResponse> getPaymentById(String id);
}
