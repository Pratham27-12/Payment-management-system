package org.example.repository;

import org.example.repository.jdbc.dao.Payment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PaymentRepository {
    CompletableFuture<Payment> getPaymentById(String id);
    CompletableFuture<Void> createPayment(Payment payment);
    CompletableFuture<Void> updatePayment(String id, Payment paymentUpdates, String userName);
    CompletableFuture<List<Payment>> getAllPayments();
    CompletableFuture<List<Payment>> findPaymentsBetween(Long startDate, Long endDate);
}
