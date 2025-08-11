package org.example.controller;

import org.example.model.response.PaymentLifeCycleManagementResponse;
import org.example.model.response.ReportResponse;
import org.example.repository.jdbc.dao.Payment;
import org.example.service.PaymentManagementService;
import org.example.service.impl.PaymentManagementServiceImpl;
import org.example.util.DeferredResultUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import static org.example.model.route.PaymentRoute.API;
import static org.example.model.route.PaymentRoute.CREATE;
import static org.example.model.route.PaymentRoute.GET_ALL;
import static org.example.model.route.PaymentRoute.MONTHLY;
import static org.example.model.route.PaymentRoute.PAYMENTS;
import static org.example.model.route.PaymentRoute.ID;
import static org.example.model.route.PaymentRoute.QUARTERLY;
import static org.example.model.route.PaymentRoute.REPORTS;
import static org.example.model.route.PaymentRoute.UPDATE;
import static org.example.model.route.PaymentRoute.V1;

@RestController
@RequestMapping(API + V1)
public class PaymentController {

    private final PaymentManagementService paymentManagementService;

    public PaymentController(PaymentManagementServiceImpl paymentManagementServiceImpl) {
        this.paymentManagementService = paymentManagementServiceImpl;
    }

    @GetMapping(PAYMENTS + ID)
    @PreAuthorize("hasRole('ADMIN') or hasRole('VIEWER') or hasRole('FINANCE_MANAGER')")
    public DeferredResult<ResponseEntity<PaymentLifeCycleManagementResponse>> getPaymentById(
            @PathVariable("id") String id) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(paymentManagementService.getPaymentById(id));
    }

    @GetMapping(PAYMENTS)
    @PreAuthorize("hasRole('ADMIN') or hasRole('VIEWER') or hasRole('FINANCE_MANAGER')")
    public DeferredResult<ResponseEntity<PaymentLifeCycleManagementResponse>> getAllPayments() {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(paymentManagementService.getAllPayment());
    }

    @PostMapping(PAYMENTS)
    @PreAuthorize("hasRole('ADMIN')")
    public DeferredResult<ResponseEntity<PaymentLifeCycleManagementResponse>> createPaymentRecord(
            @RequestBody Payment payment) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                paymentManagementService.createPaymentRecord(payment));
    }

    @PutMapping(PAYMENTS + ID)
    @PreAuthorize("hasRole('FINANCE_MANAGER')")
    public DeferredResult<ResponseEntity<PaymentLifeCycleManagementResponse>> updatePaymentRecord(
            @PathVariable("id") String id,
            @RequestBody Payment payment) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                paymentManagementService.updatePayment(id, payment.getUserName(), payment));
    }

    @GetMapping(REPORTS + MONTHLY)
    @PreAuthorize("hasRole('ADMIN') or hasRole('VIEWER') or hasRole('FINANCE_MANAGER')")
    public DeferredResult<ResponseEntity<ReportResponse>> getMonthlyReport(
            @PathVariable("month") Long month,
            @PathVariable("year") Long year
    ) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                paymentManagementService.generateMonthlyReport(month, year));
    }

    @GetMapping(REPORTS + QUARTERLY)
    @PreAuthorize("hasRole('ADMIN') or hasRole('VIEWER') or hasRole('FINANCE_MANAGER')")
    public DeferredResult<ResponseEntity<ReportResponse>> getQuarterLyReport(
            @PathVariable("quarter") Long quarter,
            @PathVariable("year") Long year
    ) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                paymentManagementService.generateQuarterlyReport(quarter, year));
    }
}
