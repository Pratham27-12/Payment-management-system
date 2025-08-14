package zeta.payments.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zeta.payments.dto.response.AuditLifeCycleManagementResponse;
import zeta.payments.service.AuditTrialManagementService;
import zeta.payments.service.impl.AuditTrialManagementServiceImpl;
import zeta.payments.util.ResponseEntityUtil;

import java.util.logging.Logger;

import static zeta.payments.commons.route.PaymentRoute.API;
import static zeta.payments.commons.route.PaymentRoute.AUDITS;
import static zeta.payments.commons.route.PaymentRoute.ID;
import static zeta.payments.commons.route.PaymentRoute.V1;

@RestController
@RequestMapping(API + V1)
public class AuditController {

    Logger logger = Logger.getLogger(AuditController.class.getName());

    private final AuditTrialManagementService auditTrialManagementService;

    public AuditController(AuditTrialManagementServiceImpl auditTrialManagementServiceImpl) {
        this.auditTrialManagementService = auditTrialManagementServiceImpl;
    }

    @GetMapping(AUDITS)
    @PreAuthorize("hasRole('ADMIN') or hasRole('VIEWER') or hasRole('FINANCE_MANAGER')")
    public ResponseEntity<AuditLifeCycleManagementResponse> getAllAudits() {
        logger.info("Fetching all payment audits");
        return ResponseEntityUtil.getResultWithResponseEntity(auditTrialManagementService.getAllPaymentAudit());
    }

    @GetMapping(AUDITS + ID)
    @PreAuthorize("hasRole('ADMIN') or hasRole('VIEWER') or hasRole('FINANCE_MANAGER')")
    public ResponseEntity<AuditLifeCycleManagementResponse> getAuditById(@PathVariable Long id) {
        logger.info("Fetching payment audit with id: " + id);
        return ResponseEntityUtil.getResultWithResponseEntity(auditTrialManagementService.getPaymentAuditById(id));
    }
}
