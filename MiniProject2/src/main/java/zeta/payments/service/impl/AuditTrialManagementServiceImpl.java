package zeta.payments.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zeta.payments.dto.response.AuditLifeCycleManagementResponse;
import zeta.payments.entity.Audit;
import zeta.payments.entity.Payment;
import zeta.payments.exception.PaymentManagementException;
import zeta.payments.repository.AuditTrialRepository;
import zeta.payments.service.AuditTrialManagementService;

import java.util.List;
import java.util.Optional;

@Component
public class AuditTrialManagementServiceImpl implements AuditTrialManagementService {

    @Autowired
    private AuditTrialRepository auditTrialRepository;

    @Override
    public AuditLifeCycleManagementResponse getPaymentAuditById(Long id) {
        try {
            List<Audit> audits = auditTrialRepository.findByPaymentId(id);
            return AuditLifeCycleManagementResponse.builder()
                    .message("Audit records fetched successfully")
                    .audits(audits)
                    .status("success")
                    .build();
        } catch (Exception e) {
            throw new PaymentManagementException(500, "An error occurred while fetching audit records", "FAILURE");
        }
    }

    @Override
    public AuditLifeCycleManagementResponse getAllPaymentAudit() {
        try {
            List<Audit> audits = auditTrialRepository.findAll();
            return AuditLifeCycleManagementResponse.builder()
                    .message("Audit records fetched successfully")
                    .audits(audits)
                    .status("success")
                    .build();
        } catch (Exception e) {
            throw new PaymentManagementException(500, "Internal Server Error", "FAILURE");
        }
    }
}
