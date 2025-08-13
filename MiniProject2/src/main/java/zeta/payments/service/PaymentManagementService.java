package zeta.payments.service;

import zeta.payments.dto.response.PaymentLifeCycleManagementResponse;
import zeta.payments.dto.response.ReportResponse;
import zeta.payments.entity.Payment;


public interface PaymentManagementService {
    PaymentLifeCycleManagementResponse createPaymentRecord(Payment payment);
    PaymentLifeCycleManagementResponse updatePayment(String id, Payment payment);
    ReportResponse generateMonthlyReport(Long month, Long year);
    ReportResponse generateQuarterlyReport(Long quarter, Long year);
    PaymentLifeCycleManagementResponse getAllPayment();
    PaymentLifeCycleManagementResponse getPaymentById(Long id);
    PaymentLifeCycleManagementResponse deletePaymentById(Long id);
}
