package org.example.repository;

import org.example.repository.jdbc.dao.AuditTrail;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AuditTrailRepository {
    CompletableFuture<List<AuditTrail>> getAuditTrailById(String id);
    CompletableFuture<List<AuditTrail>> getAuditTrailByCreatedAtRange(Long startDateEpoch, Long endDateEpoch);
}
