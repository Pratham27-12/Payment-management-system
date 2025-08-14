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
import java.util.logging.Logger;

@Component
public class AuditTrialManagementServiceImpl implements AuditTrialManagementService {

    Logger logger = Logger.getLogger(AuditTrialManagementServiceImpl.class.getName());

    @Autowired
    private AuditTrialRepository auditTrialRepository;

    @Override
    public AuditLifeCycleManagementResponse getPaymentAuditById(Long id) {
        logger.info("Fetching audit records for payment ID: " + id);
        try {
            List<Audit> audits = auditTrialRepository.findByPaymentId(id);
            logger.info("Successfully fetched " + audits.size() + " audit records");
            return AuditLifeCycleManagementResponse.builder()
                    .message("Audit records fetched successfully")
                    .audits(audits)
                    .status("success")
                    .build();
        } catch (Exception e) {
            logger.severe("Error fetching all audit records - " + e.getMessage());
            throw new PaymentManagementException(500, "An error occurred while fetching audit records", "FAILURE");
        }
    }

    @Override
    public AuditLifeCycleManagementResponse getAllPaymentAudit() {
        logger.info("Fetching all audit records");
        try {
            List<Audit> audits = auditTrialRepository.findAll();
            logger.info("Successfully fetched " + audits.size() + " audit records");
            return AuditLifeCycleManagementResponse.builder()
                    .message("Audit records fetched successfully")
                    .audits(audits)
                    .status("success")
                    .build();
        } catch (Exception e) {
            logger.severe("Error fetching all audit records - " + e.getMessage());
            throw new PaymentManagementException(500, "Internal Server Error", "FAILURE");
        }
    }
}
