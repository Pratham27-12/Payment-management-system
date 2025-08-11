package org.example.repository.jdbc.impl;

import org.example.model.enums.PaymentCategory;
import org.example.model.enums.PaymentStatus;
import org.example.model.enums.PaymentType;
import org.example.repository.PaymentRepository;
import org.example.repository.jdbc.constants.PaymentDetailsQueryConstant;
import org.example.repository.jdbc.dao.Payment;
import org.example.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.example.repository.jdbc.constants.PaymentDetailsQueryConstant.ACCOUNT_NAME;
import static org.example.repository.jdbc.constants.PaymentDetailsQueryConstant.AMOUNT;
import static org.example.repository.jdbc.constants.PaymentDetailsQueryConstant.CATEGORY;
import static org.example.repository.jdbc.constants.PaymentDetailsQueryConstant.CREATED_AT;
import static org.example.repository.jdbc.constants.PaymentDetailsQueryConstant.CURRENCY;
import static org.example.repository.jdbc.constants.PaymentDetailsQueryConstant.PAYMENT_ID;
import static org.example.repository.jdbc.constants.PaymentDetailsQueryConstant.STATUS;
import static org.example.repository.jdbc.constants.PaymentDetailsQueryConstant.TYPE;
import static org.example.repository.jdbc.constants.PaymentDetailsQueryConstant.UPDATED_AT;
import static org.example.repository.jdbc.constants.PaymentDetailsQueryConstant.CREATED_BY;

public class PaymentRepositoryImpl implements PaymentRepository {

    @Override
    public CompletableFuture<Payment> getPaymentById(String id) {
        Payment payment = null;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(PaymentDetailsQueryConstant.getPaymentDetailsById())) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    payment = mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching Payment by ID", e);
        }
        return CompletableFuture.completedFuture(payment);
    }

    @Override
    public CompletableFuture<Void> createPayment(Payment payment) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(PaymentDetailsQueryConstant.createPaymentDetailsQuery())) {
            stmt.setString(1, payment.getId());
            stmt.setString(2, payment.getUserName());
            stmt.setString(3, payment.getAccountName());
            stmt.setString(4, payment.getAmount());
            stmt.setString(5, payment.getCurrency());
            stmt.setString(6, payment.getType() != null ? payment.getType().name() : null);
            stmt.setString(7, payment.getCategory() != null ? payment.getCategory().name() : null);
            stmt.setString(8, payment.getStatus() != null ? payment.getStatus().name() : null);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating Payment", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updatePaymentStatus(String id, PaymentStatus status, String userName) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(PaymentDetailsQueryConstant.updatePaymentDetailsById())) {
            stmt.setString(1, status != null ? status.name() : null);
            stmt.setString(2, userName);
            stmt.setString(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating Payment", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<List<Payment>> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(PaymentDetailsQueryConstant.getAllPaymentDetails());
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Payment payment = mapResultSetToPayment(rs);
                payments.add(payment);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching Payments", e);
        }
        return CompletableFuture.completedFuture(payments);
    }

    @Override
    public CompletableFuture<List<Payment>> findPaymentsBetween(Long startDate, Long endDate) {
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(PaymentDetailsQueryConstant.getPaymentDetailsByCreatedAtRange())) {

            stmt.setLong(1, startDate);
            stmt.setLong(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = mapResultSetToPayment(rs);
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching Payments by Created At Range", e);
        }
        return CompletableFuture.completedFuture(payments);
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getString(PAYMENT_ID));
        payment.setUserName(rs.getString(CREATED_BY));
        payment.setAmount(rs.getString(AMOUNT));
        payment.setAccountName(rs.getString(ACCOUNT_NAME));
        payment.setCategory(rs.getString(CATEGORY) != null ? PaymentCategory.valueOf(rs.getString(CATEGORY)) : null);
        payment.setStatus(rs.getString(STATUS) != null ? PaymentStatus.valueOf(rs.getString(STATUS)) : null);
        payment.setType(rs.getString(TYPE) != null ? PaymentType.valueOf(rs.getString(TYPE)) : null);
        payment.setCurrency(rs.getString(CURRENCY));
        payment.setCreatedAt(rs.getLong(CREATED_AT));
        payment.setUpdatedAt(rs.getLong(UPDATED_AT));
        return payment;
    }

    public static void main(String[] args) {
        PaymentRepository paymentRepository = new PaymentRepositoryImpl();
        Payment dummyPayment = new Payment();
        dummyPayment.setId("PAY12345");
        dummyPayment.setAmount("15000");
        dummyPayment.setCurrency("INR");
        dummyPayment.setCategory(PaymentCategory.INVOICE);
        dummyPayment.setType(PaymentType.OUTGOING);
        dummyPayment.setStatus(PaymentStatus.PROCESSING);
        dummyPayment.setUserName("john_doe");

        paymentRepository.getAllPayments().whenComplete((res, err) ->{
            if (err != null) {
                System.err.println("Error creating payment: " + err.getMessage());
            } else {
                res.forEach(System.out::println);
            }
        });
    }

}
