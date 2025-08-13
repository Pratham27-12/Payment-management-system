package zeta.payments.dto.request;

import lombok.Data;
import zeta.payments.commons.enums.UserRole;

@Data
public class RoleChangeRequest {
    private String userName;
    private UserRole role;
}
