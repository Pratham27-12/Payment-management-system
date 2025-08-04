package org.example.model;

import org.example.repository.jdbc.dao.Payment;

import java.util.List;

public class PaymentLifeCycleManagementResponse {
    String message;
    List<Payment> payments;
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}
