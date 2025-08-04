package org.example.service.impl;

import org.example.repository.AuditTrailRepository;
import org.example.repository.jdbc.dao.AuditTrail;
import org.example.repository.jdbc.impl.AuditTrailRepositoryImpl;
import org.example.service.AuditTrailManagementService;
import org.example.util.ValidatorUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.example.util.DateUtil.convertDdMmYyyyToEpochMilli;

public class AuditTrailManagementServiceImpl implements AuditTrailManagementService {
    @Override
    public CompletableFuture<List<AuditTrail>> getAuditTrailById(String id, String password) {
        AuditTrailRepository auditTrailRepository = new AuditTrailRepositoryImpl();
        return ValidatorUtil.validateUserManager(id, password).thenCompose(validationResponse -> {
            if(validationResponse.isValid())
                return auditTrailRepository.getAuditTrailById(id);

            return CompletableFuture.failedFuture(
                    new IllegalAccessException("Only Finance Manager can access audit trail")
            );
        });
    }

    @Override
    public CompletableFuture<List<AuditTrail>> getAuditTrailByCreatedAtRange(String startDate, String endDate, String password) {
        AuditTrailRepository auditTrailRepository = new AuditTrailRepositoryImpl();
        Long startDateEpoch = convertDdMmYyyyToEpochMilli(startDate);
        Long endDateEpoch = convertDdMmYyyyToEpochMilli(endDate);
        return ValidatorUtil.validateUserManager("admin", password).thenCompose(validationResponse -> {
            if(validationResponse.isValid())
                return auditTrailRepository.getAuditTrailByCreatedAtRange(startDateEpoch, endDateEpoch);

            return CompletableFuture.failedFuture(
                    new IllegalAccessException("Only Finance Manager can access audit trail")
            );
        });
    }
}
