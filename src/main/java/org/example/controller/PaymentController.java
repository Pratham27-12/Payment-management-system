package org.example.controller;

import org.example.model.response.PaymentLifeCycleManagementResponse;
import org.example.repository.jdbc.dao.Payment;
import org.example.service.PaymentManagementService;
import org.example.service.impl.PaymentManagementServiceImpl;
import org.example.util.DeferredResultUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import static org.example.model.route.PaymentRoute.CREATE;
import static org.example.model.route.PaymentRoute.GET_ALL;
import static org.example.model.route.PaymentRoute.PAYMENTS_BASE_URL;
import static org.example.model.route.PaymentRoute.ID;
import static org.example.model.route.PaymentRoute.UPDATE;

@RestController
@RequestMapping(PAYMENTS_BASE_URL)
public class PaymentController {

    private final PaymentManagementService paymentManagementService;

    public PaymentController(PaymentManagementServiceImpl paymentManagementServiceImpl) {
        this.paymentManagementService = paymentManagementServiceImpl;
    }

    @GetMapping(ID)
    @PreAuthorize("hasRole('ADMIN') or hasRole('VIEWER') or hasRole('FINANCE_MANAGER')")
    public DeferredResult<ResponseEntity<PaymentLifeCycleManagementResponse>> getPaymentById(
            @PathVariable("id") String id) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(paymentManagementService.getPaymentById(id));
    }

    @GetMapping(GET_ALL)
    @PreAuthorize("hasRole('ADMIN') or hasRole('VIEWER') or hasRole('FINANCE_MANAGER')")
    public DeferredResult<ResponseEntity<PaymentLifeCycleManagementResponse>> getAllPayment() {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(paymentManagementService.getAllPayment());
    }

    @PostMapping(CREATE)
    @PreAuthorize("hasRole('ADMIN')")
    public DeferredResult<ResponseEntity<PaymentLifeCycleManagementResponse>> createPaymentRecord(
            @RequestBody Payment payment) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                paymentManagementService.createPaymentRecord(payment));
    }

    @PostMapping(UPDATE + ID)
    @PreAuthorize("hasRole('FINANCE_MANAGER')")
    public DeferredResult<ResponseEntity<PaymentLifeCycleManagementResponse>> updatePaymentRecord(
            @PathVariable("id") String id,
            @RequestBody Payment payment) {
        return DeferredResultUtil.getDeferredResultWithResponseEntity(
                paymentManagementService.updatePayment(id, payment.getUserName(), payment));
    }
}
