package org.example.controller;

import org.example.repository.jdbc.dao.AuditTrail;
import org.example.service.AuditTrailManagementService;
import org.example.service.impl.AuditTrailManagementServiceImpl;
import org.example.util.DeferredResultUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-trail")
public class AuditTrailController {

    private final AuditTrailManagementService auditTrailManagementService;

    public AuditTrailController(AuditTrailManagementServiceImpl auditTrailManagementServiceImpl){
        this.auditTrailManagementService = auditTrailManagementServiceImpl;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('VIEWER')")
    public DeferredResult<ResponseEntity<List<AuditTrail>>> getAuditTrialById(
            @PathVariable("id") String id) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                auditTrailManagementService.getAuditTrailById(id));
    }

    @GetMapping("/start-date/{startDate}/end-date/{endDate}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('VIEWER')")
    public DeferredResult<ResponseEntity<List<AuditTrail>>> getAuditTrailByCreatedAtRange(
            @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                auditTrailManagementService.getAuditTrailByCreatedAtRange(startDate, endDate));
        }
}
