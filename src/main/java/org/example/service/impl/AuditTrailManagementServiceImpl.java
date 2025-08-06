package org.example.service.impl;

import org.example.repository.AuditTrailRepository;
import org.example.repository.jdbc.dao.AuditTrail;
import org.example.repository.jdbc.impl.AuditTrailRepositoryImpl;
import org.example.service.AuditTrailManagementService;
import org.example.util.ValidatorUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.example.util.DateUtil.convertDdMmYyyyToEpochMilli;

@Service
public class AuditTrailManagementServiceImpl implements AuditTrailManagementService {
    private final AuditTrailRepository auditTrailRepository;

    public AuditTrailManagementServiceImpl(AuditTrailRepositoryImpl auditTrailRepositoryImpl) {
        this.auditTrailRepository = auditTrailRepositoryImpl;
    }

    @Override
    public CompletableFuture<List<AuditTrail>> getAuditTrailById(String id) {
        return auditTrailRepository.getAuditTrailById(id).thenApply(AuditTrails -> AuditTrails)
                .exceptionally(throwable -> {
                    throw new RuntimeException("Invalid credentials provided");
                });
    }

    @Override
    public CompletableFuture<List<AuditTrail>> getAuditTrailByCreatedAtRange(String startDate, String endDate) {
        Long startDateEpoch = convertDdMmYyyyToEpochMilli(startDate);
        Long endDateEpoch = convertDdMmYyyyToEpochMilli(endDate);
        return auditTrailRepository.getAuditTrailByCreatedAtRange(startDateEpoch, endDateEpoch);
    }
}
