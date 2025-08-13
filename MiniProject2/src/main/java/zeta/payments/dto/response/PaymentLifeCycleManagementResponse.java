package zeta.payments.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import zeta.payments.entity.Payment;

import java.util.List;

@Setter
@Getter
@Builder
public class PaymentLifeCycleManagementResponse {
    String message;
    List<Payment> payments;
    String status;
}
