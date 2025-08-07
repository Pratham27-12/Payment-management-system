package org.example.repository.jdbc.impl;

import org.example.model.enums.PaymentCategory;
import org.example.model.enums.PaymentStatus;
import org.example.model.enums.PaymentType;
import org.example.repository.PaymentRepository;
import org.example.repository.jdbc.constants.PaymentDetailsQueryConstant;
import org.example.repository.jdbc.dao.Payment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

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
import static org.example.repository.jdbc.constants.PaymentDetailsQueryConstant.TABLE;
import static org.example.repository.jdbc.constants.PaymentDetailsQueryConstant.TYPE;
import static org.example.repository.jdbc.constants.PaymentDetailsQueryConstant.UPDATED_AT;
import static org.example.repository.jdbc.constants.UserDetailsQueryConstant.USER_NAME;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Payment> paymentRowMapper = new RowMapper<Payment>() {
        @Override
        public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Payment.builder()
                    .id(rs.getString(PAYMENT_ID))
                    .userName(rs.getString(USER_NAME))
                    .amount(rs.getString(AMOUNT))
                    .accountName(rs.getString(ACCOUNT_NAME))
                    .category(rs.getString(CATEGORY) != null ? PaymentCategory.valueOf(rs.getString(CATEGORY)) : null)
                    .status(rs.getString(STATUS) != null ? PaymentStatus.valueOf(rs.getString(STATUS)) : null)
                    .type(rs.getString(TYPE) != null ? PaymentType.valueOf(rs.getString(TYPE)) : null)
                    .currency(rs.getString(CURRENCY))
                    .createdAt(rs.getLong(CREATED_AT))
                    .updatedAt(rs.getLong(UPDATED_AT))
                    .build();
        }
    };

    @Override
    public CompletableFuture<Payment> getPaymentById(String id) {
        try {
            List<Payment> payments = jdbcTemplate.query(
                    PaymentDetailsQueryConstant.getPaymentDetailsById(),
                    paymentRowMapper,
                    id
            );

            Payment payment = payments.isEmpty() ? null : payments.get(0);
            return CompletableFuture.completedFuture(payment);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching Payment by ID: " + id, e);
        }
    }

    @Override
    public CompletableFuture<Void> createPayment(Payment payment) {
        try {
            jdbcTemplate.update(
                    PaymentDetailsQueryConstant.createPaymentDetailsQuery(),
                    payment.getId(),
                    payment.getUserName(),
                    payment.getAccountName(),
                    payment.getAmount(),
                    payment.getCurrency(),
                    payment.getType() != null ? payment.getType().name() : null,
                    payment.getCategory() != null ? payment.getCategory().name() : null,
                    payment.getStatus() != null ? payment.getStatus().name() : null
            );
            return CompletableFuture.completedFuture(null);
        } catch (DuplicateKeyException e){
            throw new RuntimeException("Payment with ID " + payment.getId() + " already exists", e);
        } catch (Exception e) {
            throw new RuntimeException("Error creating Payment", e);
        }
    }

    @Override
    public CompletableFuture<Void> updatePayment(String id, Payment paymentUpdates, String userName) {
        try {

            StringBuilder queryBuilder = new StringBuilder(String.format("UPDATE %s SET ", TABLE));
            List<Object> parameters = new ArrayList<>();
            List<String> setClauses = new ArrayList<>();

            if (paymentUpdates.getAmount() != null) {
                setClauses.add(String.format("%s = ?", AMOUNT));
                parameters.add(paymentUpdates.getAmount());
            }

            if (paymentUpdates.getStatus() != null) {
                setClauses.add(String.format("%s = ?::payment_status", STATUS));
                parameters.add(paymentUpdates.getStatus().name());
            }

            if (paymentUpdates.getAccountName() != null) {
                setClauses.add(String.format("%s = ?", ACCOUNT_NAME));
                parameters.add(paymentUpdates.getAccountName());
            }

            if (paymentUpdates.getCurrency() != null) {
                setClauses.add(String.format("%s = ?", CURRENCY));
                parameters.add(paymentUpdates.getCurrency());
            }

            if (paymentUpdates.getType() != null) {
                setClauses.add(String.format("%s = ?::payment_type", TYPE));
                parameters.add(paymentUpdates.getType().name());
            }

            if (paymentUpdates.getCategory() != null) {
                setClauses.add(String.format("%s = ?::payment_category", CATEGORY));
                parameters.add(paymentUpdates.getCategory().name());
            }

            if (paymentUpdates.getCategory() != null) {
                setClauses.add(String.format("%s = ?", USER_NAME));
                parameters.add(userName);
            }

            if (setClauses.isEmpty()) {
                throw new RuntimeException("No fields provided for update");
            }

            queryBuilder.append(String.join(", ", setClauses));
            queryBuilder.append(String.format(" WHERE %s = ?", PAYMENT_ID));
            parameters.add(id);


            int rowsAffected = jdbcTemplate.update(queryBuilder.toString(), parameters.toArray());

            if (rowsAffected == 0) {
                throw new RuntimeException("Payment not found with ID: " + id);
            }

            return CompletableFuture.completedFuture(null);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating Payment", e);
        }
    }

    @Override
    public CompletableFuture<List<Payment>> getAllPayments() {
        try {
            List<Payment> payments = jdbcTemplate.query(
                    PaymentDetailsQueryConstant.getAllPaymentDetails(),
                    paymentRowMapper
            );
            return CompletableFuture.completedFuture(payments);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching Payments", e);
        }
    }

    @Override
    public CompletableFuture<List<Payment>> findPaymentsBetween(Long startDate, Long endDate) {
        try {
            List<Payment> payments = jdbcTemplate.query(
                    PaymentDetailsQueryConstant.getPaymentDetailsByCreatedAtRange(),
                    paymentRowMapper,
                    startDate,
                    endDate
            );
            return CompletableFuture.completedFuture(payments);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching Payments by Created At Range", e);
        }
    }
}
