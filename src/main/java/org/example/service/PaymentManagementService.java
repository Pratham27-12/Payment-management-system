package org.example.service;

import org.example.model.response.PaymentLifeCycleManagementResponse;
import org.example.model.response.ReportResponse;
import org.example.repository.jdbc.dao.Payment;

import java.util.concurrent.CompletableFuture;

public interface PaymentManagementService {
    CompletableFuture<PaymentLifeCycleManagementResponse> createPaymentRecord(Payment payment);
    CompletableFuture<PaymentLifeCycleManagementResponse> updatePayment(String id, String userName, Payment payment);
    CompletableFuture<ReportResponse> generateMonthlyReport(Long month, Long year);
    CompletableFuture<ReportResponse> generateQuarterlyReport(Long quarter, Long year);
    CompletableFuture<PaymentLifeCycleManagementResponse> getAllPayment();
    CompletableFuture<PaymentLifeCycleManagementResponse> getPaymentById(String id);
}
