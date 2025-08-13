package zeta.payments.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class PaymentExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(PaymentManagementException.class)
    public ResponseEntity<ErrorResponse> handleTisMandateException(PaymentManagementException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ErrorResponse.builder()
                .code(ex.getHttpStatus())
                .status(ex.getStatus())
                .message(ex.getDescription())
                .build());
    }
}
