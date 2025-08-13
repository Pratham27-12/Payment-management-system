package zeta.payments.service;

import zeta.payments.dto.response.AuditLifeCycleManagementResponse;

public interface AuditTrialManagementService {
    AuditLifeCycleManagementResponse getPaymentAuditById(Long id);
    AuditLifeCycleManagementResponse getAllPaymentAudit();
}
