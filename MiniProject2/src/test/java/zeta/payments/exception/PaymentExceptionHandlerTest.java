package zeta.payments.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentExceptionHandlerTest {

    @InjectMocks
    private PaymentExceptionHandler paymentExceptionHandler;

    private PaymentManagementException testException;

    @BeforeEach
    void setUp() {
        paymentExceptionHandler = new PaymentExceptionHandler();
    }

    @Test
    void handleTisMandateException_BadRequestException_ReturnsCorrectResponse() {
        testException = new PaymentManagementException(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid payment data",
                "BAD_REQUEST"
        );

        ResponseEntity<ErrorResponse> response = paymentExceptionHandler.handleTisMandateException(testException);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
        assertEquals("BAD_REQUEST", errorResponse.getStatus());
        assertEquals("Invalid payment data", errorResponse.getMessage());
    }

    @Test
    void handleTisMandateException_NotFoundException_ReturnsCorrectResponse() {
        testException = new PaymentManagementException(
                HttpStatus.NOT_FOUND.value(),
                "Payment not found",
                "NOT_FOUND"
        );

        ResponseEntity<ErrorResponse> response = paymentExceptionHandler.handleTisMandateException(testException);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getCode());
        assertEquals("NOT_FOUND", errorResponse.getStatus());
        assertEquals("Payment not found", errorResponse.getMessage());
    }

    @Test
    void handleTisMandateException_ConflictException_ReturnsCorrectResponse() {
        testException = new PaymentManagementException(
                HttpStatus.CONFLICT.value(),
                "Payment already exists",
                "CONFLICT"
        );

        ResponseEntity<ErrorResponse> response = paymentExceptionHandler.handleTisMandateException(testException);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getCode());
        assertEquals("CONFLICT", errorResponse.getStatus());
        assertEquals("Payment already exists", errorResponse.getMessage());
    }

    @Test
    void handleTisMandateException_InternalServerErrorException_ReturnsCorrectResponse() {
        testException = new PaymentManagementException(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error occurred",
                "INTERNAL_SERVER_ERROR"
        );

        ResponseEntity<ErrorResponse> response = paymentExceptionHandler.handleTisMandateException(testException);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getCode());
        assertEquals("INTERNAL_SERVER_ERROR", errorResponse.getStatus());
        assertEquals("Internal server error occurred", errorResponse.getMessage());
    }

    @Test
    void handleTisMandateException_UnauthorizedException_ReturnsCorrectResponse() {
        testException = new PaymentManagementException(
                HttpStatus.UNAUTHORIZED.value(),
                "User not authorized",
                "UNAUTHORIZED"
        );

        ResponseEntity<ErrorResponse> response = paymentExceptionHandler.handleTisMandateException(testException);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.getCode());
        assertEquals("UNAUTHORIZED", errorResponse.getStatus());
        assertEquals("User not authorized", errorResponse.getMessage());
    }

    @Test
    void handleTisMandateException_ForbiddenException_ReturnsCorrectResponse() {
        testException = new PaymentManagementException(
                HttpStatus.FORBIDDEN.value(),
                "Access forbidden",
                "FORBIDDEN"
        );

        ResponseEntity<ErrorResponse> response = paymentExceptionHandler.handleTisMandateException(testException);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.FORBIDDEN.value(), errorResponse.getCode());
        assertEquals("FORBIDDEN", errorResponse.getStatus());
        assertEquals("Access forbidden", errorResponse.getMessage());
    }

    @Test
    void handleTisMandateException_WithNullStatus_HandlesGracefully() {
        testException = new PaymentManagementException(
                HttpStatus.BAD_REQUEST.value(),
                "Error with null status"
        );

        ResponseEntity<ErrorResponse> response = paymentExceptionHandler.handleTisMandateException(testException);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
        assertNull(errorResponse.getStatus());
        assertEquals("Error with null status", errorResponse.getMessage());
    }

    @Test
    void handleTisMandateException_WithEmptyStrings_HandlesGracefully() {
        testException = new PaymentManagementException(
                HttpStatus.BAD_REQUEST.value(),
                "",
                ""
        );

        ResponseEntity<ErrorResponse> response = paymentExceptionHandler.handleTisMandateException(testException);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
        assertEquals("", errorResponse.getStatus());
        assertEquals("", errorResponse.getMessage());
    }

    @Test
    void handleTisMandateException_WithLongMessage_HandlesGracefully() {
        String longMessage = "This is a very long error message that contains detailed information about what went wrong during the payment processing operation and includes multiple details about the error condition.";

        testException = new PaymentManagementException(
                HttpStatus.BAD_REQUEST.value(),
                longMessage,
                "BAD_REQUEST"
        );

        ResponseEntity<ErrorResponse> response = paymentExceptionHandler.handleTisMandateException(testException);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
        assertEquals("BAD_REQUEST", errorResponse.getStatus());
        assertEquals(longMessage, errorResponse.getMessage());
    }

    @Test
    void handleTisMandateException_WithSpecialCharacters_HandlesGracefully() {
        String specialMessage = "Error with special characters: @#$%^&*()_+-={}[]|\\:;\"'<>?,./";
        String specialStatus = "STATUS_WITH_SPECIAL_CHARS_!@#";

        testException = new PaymentManagementException(
                HttpStatus.BAD_REQUEST.value(),
                specialMessage,
                specialStatus
        );

        ResponseEntity<ErrorResponse> response = paymentExceptionHandler.handleTisMandateException(testException);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
        assertEquals(specialStatus, errorResponse.getStatus());
        assertEquals(specialMessage, errorResponse.getMessage());
    }

    @Test
    void handleTisMandateException_WithUnicodeCharacters_HandlesGracefully() {
        String unicodeMessage = "Error message with unicode: αβγδε 中文 العربية русский";
        String unicodeStatus = "UNICODE_STATUS_αβγ";

        testException = new PaymentManagementException(
                HttpStatus.BAD_REQUEST.value(),
                unicodeMessage,
                unicodeStatus
        );

        ResponseEntity<ErrorResponse> response = paymentExceptionHandler.handleTisMandateException(testException);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
        assertEquals(unicodeStatus, errorResponse.getStatus());
        assertEquals(unicodeMessage, errorResponse.getMessage());
    }

    @Test
    void handleTisMandateException_VerifyErrorResponseBuilder() {
        testException = new PaymentManagementException(
                HttpStatus.BAD_REQUEST.value(),
                "TEST_STATUS",
                "Test message"
        );

        ResponseEntity<ErrorResponse> response = paymentExceptionHandler.handleTisMandateException(testException);

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);

        // Verify that ErrorResponse is properly built with all fields
        assertNotNull(errorResponse.getCode());
        assertNotNull(errorResponse.getStatus());
        assertNotNull(errorResponse.getMessage());

        // Verify the builder pattern works correctly
        assertEquals(testException.getHttpStatus(), errorResponse.getCode());
        assertEquals(testException.getStatus(), errorResponse.getStatus());
        assertEquals(testException.getDescription(), errorResponse.getMessage());
    }

    @Test
    void handleTisMandateException_VerifyResponseEntityStatusCode() {
        // Test different HTTP status codes
        HttpStatus[] statusCodes = {
                HttpStatus.BAD_REQUEST,
                HttpStatus.UNAUTHORIZED,
                HttpStatus.FORBIDDEN,
                HttpStatus.NOT_FOUND,
                HttpStatus.CONFLICT,
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.SERVICE_UNAVAILABLE,
                HttpStatus.BAD_GATEWAY
        };

        for (HttpStatus statusCode : statusCodes) {
            testException = new PaymentManagementException(
                    statusCode.value(),
                    "Test message for " + statusCode.name(),
                    statusCode.name()
            );

            ResponseEntity<ErrorResponse> response = paymentExceptionHandler.handleTisMandateException(testException);

            assertEquals(statusCode, response.getStatusCode());
            assertEquals(statusCode.value(), response.getBody().getCode());
        }
    }

    @Test
    void handleTisMandateException_VerifyInheritanceFromResponseEntityExceptionHandler() {
        // Verify that PaymentExceptionHandler extends ResponseEntityExceptionHandler
        assertTrue(paymentExceptionHandler instanceof org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler);
    }

    @Test
    void handleTisMandateException_MultipleCallsWithSameException_ConsistentResults() {
        testException = new PaymentManagementException(
                HttpStatus.BAD_REQUEST.value(),
                "CONSISTENT_TEST",
                "Consistency test message"
        );

        ResponseEntity<ErrorResponse> response1 = paymentExceptionHandler.handleTisMandateException(testException);
        ResponseEntity<ErrorResponse> response2 = paymentExceptionHandler.handleTisMandateException(testException);
        ResponseEntity<ErrorResponse> response3 = paymentExceptionHandler.handleTisMandateException(testException);

        // All responses should be identical
        assertEquals(response1.getStatusCode(), response2.getStatusCode());
        assertEquals(response2.getStatusCode(), response3.getStatusCode());

        assertEquals(response1.getBody().getCode(), response2.getBody().getCode());
        assertEquals(response2.getBody().getCode(), response3.getBody().getCode());

        assertEquals(response1.getBody().getStatus(), response2.getBody().getStatus());
        assertEquals(response2.getBody().getStatus(), response3.getBody().getStatus());

        assertEquals(response1.getBody().getMessage(), response2.getBody().getMessage());
        assertEquals(response2.getBody().getMessage(), response3.getBody().getMessage());
    }
}