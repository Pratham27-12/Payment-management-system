package zeta.payments.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import zeta.payments.entity.User;

import java.util.List;

@Setter
@Builder
@Getter
public class UserLifeCycleManagementResponse {
    String status;
    String message;
    List<User> users;
}
