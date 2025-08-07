package org.example.repository.jdbc.dao;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.PaymentCategory;
import org.example.model.enums.PaymentStatus;
import org.example.model.enums.PaymentType;

@Getter
@Setter
@Builder
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
}
