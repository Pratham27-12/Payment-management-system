package zeta.payments.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import zeta.payments.entity.Payment;
import zeta.payments.dto.response.PaymentLifeCycleManagementResponse;
import zeta.payments.dto.response.ReportResponse;
import zeta.payments.service.PaymentManagementService;
import zeta.payments.service.impl.PaymentManagementServiceImpl;
import zeta.payments.util.ResponseEntityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static zeta.payments.commons.route.PaymentRoute.API;
import static zeta.payments.commons.route.PaymentRoute.MONTHLY;
import static zeta.payments.commons.route.PaymentRoute.PAYMENTS;
import static zeta.payments.commons.route.PaymentRoute.ID;
import static zeta.payments.commons.route.PaymentRoute.QUARTERLY;
import static zeta.payments.commons.route.PaymentRoute.REPORTS;
import static zeta.payments.commons.route.PaymentRoute.V1;

@RestController
@RequestMapping(API + V1)
public class PaymentController {

    private final PaymentManagementService paymentManagementService;

    public PaymentController(PaymentManagementServiceImpl paymentManagementServiceImpl) {
        this.paymentManagementService = paymentManagementServiceImpl;
    }

    @GetMapping(PAYMENTS + ID)
    @PreAuthorize("hasRole('ADMIN') or hasRole('VIEWER') or hasRole('FINANCE_MANAGER')")
    public ResponseEntity<PaymentLifeCycleManagementResponse> getPaymentById(
            @PathVariable("id") Long id) {
        return ResponseEntityUtil.getResultWithResponseEntity(paymentManagementService.getPaymentById(id));
    }

    @GetMapping(PAYMENTS)
    @PreAuthorize("hasRole('ADMIN') or hasRole('VIEWER') or hasRole('FINANCE_MANAGER')")
    public ResponseEntity<PaymentLifeCycleManagementResponse> getAllPayments() {
        return ResponseEntityUtil.getResultWithResponseEntity(paymentManagementService.getAllPayment());
    }

    @PostMapping(PAYMENTS)
    @PreAuthorize("hasRole('FINANCE_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<PaymentLifeCycleManagementResponse> createPaymentRecord(
            @RequestBody Payment payment) {
        return ResponseEntityUtil.getResultWithResponseEntity(paymentManagementService.createPaymentRecord(payment));
    }

    @PutMapping(PAYMENTS + ID)
    @PreAuthorize("hasRole('FINANCE_MANAGER')")
    public ResponseEntity<PaymentLifeCycleManagementResponse> updatePaymentRecord(
            @PathVariable("id") String id,
            @RequestBody Payment payment) {
        return ResponseEntityUtil.getResultWithResponseEntity(paymentManagementService.updatePayment(id, payment));
    }

    @GetMapping(REPORTS + MONTHLY)
    @PreAuthorize("hasRole('FINANCE_MANAGER')")
    public ResponseEntity<ReportResponse> getMonthlyReport(
            @PathVariable("month") Long month,
            @PathVariable("year") Long year
    ) {
        return ResponseEntityUtil.getResultWithResponseEntity(paymentManagementService.generateMonthlyReport(month, year));
    }

    @GetMapping(REPORTS + QUARTERLY)
    @PreAuthorize("hasRole('FINANCE_MANAGER')")
    public ResponseEntity<ReportResponse> getQuarterLyReport(
            @PathVariable("quarter") Long quarter,
            @PathVariable("year") Long year
    ) {
        return ResponseEntityUtil.getResultWithResponseEntity(paymentManagementService.generateQuarterlyReport(quarter, year));
    }

    @DeleteMapping(PAYMENTS + ID)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentLifeCycleManagementResponse> deletePaymentById(
            @PathVariable("id") Long id) {
        return ResponseEntityUtil.getResultWithResponseEntity(paymentManagementService.deletePaymentById(id));
    }
}
