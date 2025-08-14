package zeta.payments.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class PaymentManagementExceptionTest {

    @Test
    void constructor_WithMessage_SetsDescriptionCorrectly() {
        String message = "Test error message";

        PaymentManagementException exception = new PaymentManagementException(message);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getDescription());
        assertNull(exception.getHttpStatus());
        assertNull(exception.getStatus());
    }

    @Test
    void constructor_WithMessageAndThrowable_SetsFieldsCorrectly() {
        String message = "Test error with cause";
        Throwable cause = new IllegalArgumentException("Root cause");

        PaymentManagementException exception = new PaymentManagementException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getDescription());
        assertEquals(cause, exception.getCause());
        assertNull(exception.getHttpStatus());
        assertNull(exception.getStatus());
    }

    @Test
    void constructor_WithHttpStatusMessageAndStatus_SetsAllFieldsCorrectly() {
        Integer httpStatus = HttpStatus.BAD_REQUEST.value();
        String message = "Bad request error";
        String status = "BAD_REQUEST";

        PaymentManagementException exception = new PaymentManagementException(httpStatus, message, status);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getDescription());
        assertEquals(httpStatus, exception.getHttpStatus());
        assertEquals(status, exception.getStatus());
    }

    @Test
    void constructor_WithHttpStatusAndMessage_SetsFieldsCorrectly() {
        Integer httpStatus = HttpStatus.NOT_FOUND.value();
        String message = "Resource not found";

        PaymentManagementException exception = new PaymentManagementException(httpStatus, message);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getDescription());
        assertEquals(httpStatus, exception.getHttpStatus());
        assertNull(exception.getStatus());
    }

    @Test
    void constructor_WithHttpStatusMessageAndThrowable_SetsFieldsCorrectly() {
        Integer httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = "Internal server error";
        Throwable cause = new RuntimeException("Database connection failed");

        PaymentManagementException exception = new PaymentManagementException(httpStatus, message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getDescription());
        assertEquals(httpStatus, exception.getHttpStatus());
        assertEquals(cause, exception.getCause());
        assertNull(exception.getStatus());
    }

    @Test
    void constructor_WithNullMessage_HandlesGracefully() {
        PaymentManagementException exception = new PaymentManagementException((String) null);

        assertNull(exception.getMessage());
        assertNull(exception.getDescription());
        assertNull(exception.getHttpStatus());
        assertNull(exception.getStatus());
    }

    @Test
    void constructor_WithEmptyMessage_HandlesGracefully() {
        String emptyMessage = "";

        PaymentManagementException exception = new PaymentManagementException(emptyMessage);

        assertEquals(emptyMessage, exception.getMessage());
        assertEquals(emptyMessage, exception.getDescription());
        assertNull(exception.getHttpStatus());
        assertNull(exception.getStatus());
    }

    @Test
    void constructor_WithNullHttpStatus_HandlesGracefully() {
        String message = "Error with null status";

        PaymentManagementException exception = new PaymentManagementException((Integer) null, message);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getDescription());
        assertNull(exception.getHttpStatus());
        assertNull(exception.getStatus());
    }

    @Test
    void constructor_WithNullStatus_HandlesGracefully() {
        Integer httpStatus = HttpStatus.BAD_REQUEST.value();
        String message = "Error with null status string";

        PaymentManagementException exception = new PaymentManagementException(httpStatus, message, (String) null);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getDescription());
        assertEquals(httpStatus, exception.getHttpStatus());
        assertNull(exception.getStatus());
    }

    @Test
    void constructor_WithNullThrowable_HandlesGracefully() {
        String message = "Error with null throwable";

        PaymentManagementException exception = new PaymentManagementException(message, (Throwable) null);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getDescription());
        assertNull(exception.getCause());
        assertNull(exception.getHttpStatus());
        assertNull(exception.getStatus());
    }

    @Test
    void constructor_WithSpecialCharacters_HandlesCorrectly() {
        String specialMessage = "Error with special chars: @#$%^&*()";
        String specialStatus = "STATUS_WITH_SPECIAL!@#";
        Integer httpStatus = HttpStatus.BAD_REQUEST.value();

        PaymentManagementException exception = new PaymentManagementException(httpStatus, specialMessage, specialStatus);

        assertEquals(specialMessage, exception.getMessage());
        assertEquals(specialMessage, exception.getDescription());
        assertEquals(httpStatus, exception.getHttpStatus());
        assertEquals(specialStatus, exception.getStatus());
    }

    @Test
    void constructor_WithUnicodeCharacters_HandlesCorrectly() {
        String unicodeMessage = "Error: αβγδε 中文 العربية";
        String unicodeStatus = "UNICODE_αβγ";
        Integer httpStatus = HttpStatus.BAD_REQUEST.value();

        PaymentManagementException exception = new PaymentManagementException(httpStatus, unicodeMessage, unicodeStatus);

        assertEquals(unicodeMessage, exception.getMessage());
        assertEquals(unicodeMessage, exception.getDescription());
        assertEquals(httpStatus, exception.getHttpStatus());
        assertEquals(unicodeStatus, exception.getStatus());
    }

    @Test
    void constructor_WithLongMessage_HandlesCorrectly() {
        String longMessage = "This is a very long error message that contains detailed information about what went wrong during the payment processing operation and includes multiple details about the error condition that occurred while processing the payment request.";

        PaymentManagementException exception = new PaymentManagementException(longMessage);

        assertEquals(longMessage, exception.getMessage());
        assertEquals(longMessage, exception.getDescription());
    }

    @Test
    void getters_ReturnCorrectValues() {
        Integer httpStatus = HttpStatus.FORBIDDEN.value();
        String message = "Access forbidden";
        String status = "FORBIDDEN";
        Throwable cause = new SecurityException("Access denied");

        PaymentManagementException exception = new PaymentManagementException(httpStatus, message, cause, status);

        assertEquals(httpStatus, exception.getHttpStatus());
        assertEquals(message, exception.getDescription());
        assertEquals(status, exception.getStatus());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void inheritance_ExtendsRuntimeException() {
        PaymentManagementException exception = new PaymentManagementException("Test");

        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    void constructor_WithZeroHttpStatus_HandlesCorrectly() {
        Integer httpStatus = 0;
        String message = "Error with zero status";

        PaymentManagementException exception = new PaymentManagementException(httpStatus, message);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getDescription());
        assertEquals(httpStatus, exception.getHttpStatus());
    }

    @Test
    void constructor_WithNegativeHttpStatus_HandlesCorrectly() {
        Integer httpStatus = -1;
        String message = "Error with negative status";

        PaymentManagementException exception = new PaymentManagementException(httpStatus, message);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getDescription());
        assertEquals(httpStatus, exception.getHttpStatus());
    }

    @Test
    void constructor_WithAllHttpStatusCodes_HandlesCorrectly() {
        HttpStatus[] statusCodes = {
                HttpStatus.OK,
                HttpStatus.BAD_REQUEST,
                HttpStatus.UNAUTHORIZED,
                HttpStatus.FORBIDDEN,
                HttpStatus.NOT_FOUND,
                HttpStatus.CONFLICT,
                HttpStatus.INTERNAL_SERVER_ERROR
        };

        for (HttpStatus statusCode : statusCodes) {
            String message = "Error for status " + statusCode.value();

            PaymentManagementException exception = new PaymentManagementException(
                    statusCode.value(),
                    message,
                    statusCode.name()
            );

            assertEquals(statusCode.value(), exception.getHttpStatus());
            assertEquals(message, exception.getDescription());
            assertEquals(statusCode.name(), exception.getStatus());
        }
    }
}