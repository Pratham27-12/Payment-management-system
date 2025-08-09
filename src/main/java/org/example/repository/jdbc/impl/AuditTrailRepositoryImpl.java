package org.example.repository.jdbc.impl;

import org.example.model.enums.PaymentCategory;
import org.example.model.enums.PaymentStatus;
import org.example.model.enums.PaymentType;
import org.example.repository.AuditTrailRepository;
import org.example.repository.jdbc.constants.AuditTrailQueryConstant;
import org.example.repository.jdbc.dao.AuditTrail;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import static org.example.repository.jdbc.constants.AuditTrailQueryConstant.CREATED_BY;

@Component
public class AuditTrailRepositoryImpl implements AuditTrailRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuditTrailRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AuditTrail> auditTrailRowMapper = new RowMapper<AuditTrail>() {
        @Override
        public AuditTrail mapRow(ResultSet rs, int rowNum) throws SQLException {
            return AuditTrail.builder()
                    .id(rs.getString(PAYMENT_ID))
                    .amount(rs.getString(AMOUNT))
                    .category(PaymentCategory.valueOf(rs.getString(CATEGORY)))
                    .type(PaymentType.valueOf(rs.getString(TYPE)))
                    .status(PaymentStatus.valueOf(rs.getString(STATUS)))
                    .userName(rs.getString(CREATED_BY))
                    .createdAt(rs.getLong(CREATED_AT))
                    .updatedAt(rs.getLong(UPDATED_AT))
                    .currency(rs.getString(CURRENCY))
                    .build();
        }
    };

    @Override
    public CompletableFuture<List<AuditTrail>> getAuditTrailById(String id) {
        try {
            List<AuditTrail> auditTrails = jdbcTemplate.query(
                    AuditTrailQueryConstant.getAuditTrailById(),
                    auditTrailRowMapper,
                    id
            );

            return CompletableFuture.completedFuture(auditTrails);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching audit trail by ID: " + id, e);
        }
    }

    @Override
    public CompletableFuture<List<AuditTrail>> getAuditTrailByCreatedAtRange(Long startDateEpoch, Long endDateEpoch) {
        try {
            List<AuditTrail> auditTrails = jdbcTemplate.query(
                    AuditTrailQueryConstant.getAuditTrailByCreatedAtRange(),
                    auditTrailRowMapper,
                    startDateEpoch,
                    endDateEpoch
            );

            return CompletableFuture.completedFuture(auditTrails);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching audit trails by date range: ", e);
        }
    }
}
