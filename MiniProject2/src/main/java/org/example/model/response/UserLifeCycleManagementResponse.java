package org.example.model.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.repository.jdbc.dao.User;

import java.util.List;

@Setter
@Builder
@Getter
public class UserLifeCycleManagementResponse {
    String status;
    String message;
    List<User> users;
}
