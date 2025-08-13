package zeta.payments.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import zeta.payments.entity.Audit;

import java.util.List;

@Setter
@Getter
@Builder
public class AuditLifeCycleManagementResponse {
    String message;
    List<Audit> audits;
    String status;
}
