package org.example.repository.jdbc.dao;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.PaymentCategory;
import org.example.model.enums.PaymentStatus;
import org.example.model.enums.PaymentType;

@Setter
@Getter
@Builder
public class Payment {
    private String id;
    private String amount;
    private String currency;
    private PaymentCategory category;
    private PaymentType type;
    private PaymentStatus status;
    private String accountName;
    private String userName;
    private Long createdAt;
    private Long updatedAt;
}
