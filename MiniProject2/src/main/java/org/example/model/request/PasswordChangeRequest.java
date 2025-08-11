package org.example.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordChangeRequest {
    private String userName;
    private String oldPassword;
    private String newPassword;
}
