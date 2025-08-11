package org.example.service;

import org.example.repository.jdbc.dao.AuditTrail;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AuditTrailManagementService {
    CompletableFuture<List<AuditTrail>> getAuditTrailById(String id, String username, String password);
    CompletableFuture<List<AuditTrail>> getAuditTrailByCreatedAtRange(String startDate, String endDate, String password);
}
