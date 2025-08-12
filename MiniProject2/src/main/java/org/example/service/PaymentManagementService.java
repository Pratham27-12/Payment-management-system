package org.example.service;

import org.example.model.response.PaymentLifeCycleManagementResponse;
import org.example.model.response.ReportResponse;
import org.example.model.dto.Payment;


public interface PaymentManagementService {
    PaymentLifeCycleManagementResponse createPaymentRecord(Payment payment);
    PaymentLifeCycleManagementResponse updatePayment(String id, String userName, Payment payment);
    ReportResponse generateMonthlyReport(Long month, Long year);
    ReportResponse generateQuarterlyReport(Long quarter, Long year);
    PaymentLifeCycleManagementResponse getAllPayment();
    PaymentLifeCycleManagementResponse getPaymentById(String id);
}
