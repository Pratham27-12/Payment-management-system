package org.example.repository.jdbc.impl;

import org.example.model.enums.PaymentCategory;
import org.example.model.enums.PaymentStatus;
import org.example.model.enums.PaymentType;
import org.example.repository.AuditTrailRepository;
import org.example.repository.jdbc.constants.AuditTrailQueryConstant;
import org.example.repository.jdbc.dao.AuditTrail;
import org.example.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.example.repository.jdbc.constants.AuditTrailQueryConstant.AMOUNT;
import static org.example.repository.jdbc.constants.AuditTrailQueryConstant.CATEGORY;
import static org.example.repository.jdbc.constants.AuditTrailQueryConstant.CREATED_AT;
import static org.example.repository.jdbc.constants.AuditTrailQueryConstant.CURRENCY;
import static org.example.repository.jdbc.constants.AuditTrailQueryConstant.PAYMENT_ID;
import static org.example.repository.jdbc.constants.AuditTrailQueryConstant.STATUS;
import static org.example.repository.jdbc.constants.AuditTrailQueryConstant.TYPE;
import static org.example.repository.jdbc.constants.AuditTrailQueryConstant.UPDATED_AT;
import static org.example.repository.jdbc.constants.AuditTrailQueryConstant.USER_NAME;

public class AuditTrailRepositoryImpl implements AuditTrailRepository {

    @Override
    public CompletableFuture<List<AuditTrail>> getAuditTrailById(String id) {
        List<AuditTrail> auditTrails = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AuditTrailQueryConstant.getAuditTrailById())) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    auditTrails.add(mapResultSetToAuditTrail(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching audit trail by ID: " + id, e);
        }

        return CompletableFuture.completedFuture(auditTrails);
    }

    @Override
    public CompletableFuture<List<AuditTrail>> getAuditTrailByCreatedAtRange(Long startDateEpoch, Long endDateEpoch) {
        List<AuditTrail> auditTrails = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AuditTrailQueryConstant.getAuditTrailByCreatedAtRange())) {

            stmt.setLong(1, startDateEpoch);
            stmt.setLong(2, endDateEpoch);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    auditTrails.add(mapResultSetToAuditTrail(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching audit trails in date range", e);
        }

        return CompletableFuture.completedFuture(auditTrails);

    }

    private AuditTrail mapResultSetToAuditTrail(ResultSet rs) throws SQLException {
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setId(rs.getString(PAYMENT_ID));
        auditTrail.setAmount(rs.getString(AMOUNT));
        auditTrail.setCurrency(rs.getString(CURRENCY));
        auditTrail.setUserName(rs.getString(USER_NAME));
        auditTrail.setCreatedAt(rs.getString(CREATED_AT));
        auditTrail.setUpdatedAt(rs.getString(UPDATED_AT));
        String category = rs.getString(CATEGORY);
        String status = rs.getString(STATUS);
        String type = rs.getString(TYPE);
        auditTrail.setCategory(category != null ? PaymentCategory.valueOf(category) : null);
        auditTrail.setStatus(status != null ? PaymentStatus.valueOf(status) : null);
        auditTrail.setType(type != null ? PaymentType.valueOf(type) : null);
        return auditTrail;
    }

    public static void main(String[] args) {
        AuditTrailRepository repository = new AuditTrailRepositoryImpl();

        repository.getAuditTrailById("PAY12345").whenComplete((res, err) -> {
            if (err != null)
                System.err.println("Error fetching audit trail by ID: " + err.getMessage());

            System.out.println("Audit Trail for ID PAY12345:");
            res.forEach(System.out::println);
        });
    }
}
