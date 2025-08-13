package zeta.payments.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    private int code;
    private String message;
    private String status;

    public ErrorResponse(String message)
    {
        super();
        this.message = message;
    }
}
