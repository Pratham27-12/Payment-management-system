package org.example.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.model.dto.Payment;

import java.util.List;

@Setter
@Getter
@Builder
public class PaymentLifeCycleManagementResponse {
    String message;
    List<Payment> payments;
    String status;
}
