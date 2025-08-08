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

import static org.example.model.route.PaymentRoute.AUDIT_TRAIL_BASE_URL;
import static org.example.model.route.PaymentRoute.END_DATE;
import static org.example.model.route.PaymentRoute.ID;
import static org.example.model.route.PaymentRoute.START_DATE;

@RestController
@RequestMapping(AUDIT_TRAIL_BASE_URL)
public class AuditTrailController {

    private final AuditTrailManagementService auditTrailManagementService;

    public AuditTrailController(AuditTrailManagementServiceImpl auditTrailManagementServiceImpl) {
        this.auditTrailManagementService = auditTrailManagementServiceImpl;
    }

    @GetMapping(ID)
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('VIEWER')")
    public DeferredResult<ResponseEntity<List<AuditTrail>>> getAuditTrialById(
            @PathVariable("id") String id) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                auditTrailManagementService.getAuditTrailById(id));
    }

    @GetMapping(START_DATE + END_DATE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER') or hasRole('VIEWER')")
    public DeferredResult<ResponseEntity<List<AuditTrail>>> getAuditTrailByCreatedAtRange(
            @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                auditTrailManagementService.getAuditTrailByCreatedAtRange(startDate, endDate));
    }
}
