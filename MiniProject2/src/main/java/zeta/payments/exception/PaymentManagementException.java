package zeta.payments.exception;

import lombok.Getter;

@Getter
public class PaymentManagementException extends RuntimeException {

    private Integer httpStatus;
    private String description;
    private String status;

    public PaymentManagementException(String message) {
        super(message);
        this.description = message;
    }

    public PaymentManagementException(String message, Throwable throwable) {
        super(message, throwable);
        this.description = message;
    }

    public PaymentManagementException(Integer httpStatus, String message, String status) {
        super(message);
        this.description = message;
        this.httpStatus = httpStatus;
        this.status = status;
    }

    public PaymentManagementException(Integer httpStatus, String message) {
        super(message);
        this.description = message;
        this.httpStatus = httpStatus;
    }

    public PaymentManagementException(Integer httpStatus, String message, Throwable throwable) {
        super(message, throwable);
        this.description = message;
        this.httpStatus = httpStatus;
    }

    public PaymentManagementException(Integer httpStatus, String message, Throwable throwable, String status) {
        super(message, throwable);
        this.description = message;
        this.httpStatus = httpStatus;
        this.status = status;
    }
}


