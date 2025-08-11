package org.example.repository.jdbc.dao;

import org.example.model.enums.PaymentCategory;
import org.example.model.enums.PaymentStatus;
import org.example.model.enums.PaymentType;

public class AuditTrail {
    private String id;
    private String userName;
    private String amount;
    private String currency;
    private PaymentCategory category;
    private PaymentType type;
    private PaymentStatus status;
    private Long createdAt;
    private Long updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentCategory getCategory() {
        return category;
    }

    public void setCategory(PaymentCategory category) {
        this.category = category;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "AuditTrail{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", amount='" + amount + '\'' +
                ", currency='" + currency + '\'' +
                ", category=" + category +
                ", type=" + type +
                ", status=" + status +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
