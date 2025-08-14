package zeta.payments.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import zeta.payments.commons.enums.PaymentType;
import zeta.payments.dto.response.PaymentLifeCycleManagementResponse;
import zeta.payments.dto.response.ReportResponse;
import zeta.payments.entity.Payment;
import zeta.payments.exception.PaymentManagementException;
import zeta.payments.repository.PaymentRepository;
import zeta.payments.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentManagementServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PaymentManagementServiceImpl paymentManagementService;

    private Payment testPayment;
    private Map<String, Double> exchangeRates;

    @BeforeEach
    void setUp() {
        exchangeRates = Map.of("USD", 83.0, "EUR", 90.0);
        paymentManagementService = new PaymentManagementServiceImpl(exchangeRates);
        org.springframework.test.util.ReflectionTestUtils.setField(paymentManagementService, "paymentRepository", paymentRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(paymentManagementService, "userRepository", userRepository);

        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setAmount("1000.0");
        testPayment.setCurrency("INR");
        testPayment.setType(PaymentType.INCOMING);
        testPayment.setCreatedAt(System.currentTimeMillis());
        testPayment.setCreatedBy("testuser");
    }

    @Test
    void createPaymentRecord_Success() {
        when(paymentRepository.saveAndFlush(testPayment)).thenReturn(testPayment);

        PaymentLifeCycleManagementResponse response = paymentManagementService.createPaymentRecord(testPayment);

        assertNotNull(response);
        assertEquals("Payment Created Successfully", response.getMessage());
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(1, response.getPayments().size());
        verify(paymentRepository).saveAndFlush(testPayment);
    }

    @Test
    void createPaymentRecord_Exception() {
        when(paymentRepository.saveAndFlush(testPayment)).thenThrow(new RuntimeException("Database error"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> paymentManagementService.createPaymentRecord(testPayment));

        assertEquals(500, exception.getHttpStatus());
        assertTrue(exception.getMessage().contains("Internal Server Error"));
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void updatePayment_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("updatedUser");

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            PaymentLifeCycleManagementResponse response = paymentManagementService.updatePayment("1", testPayment);

            assertNotNull(response);
            assertEquals("Payment Status Updated Successfully", response.getMessage());
            assertEquals("SUCCESS", response.getStatus());
            verify(paymentRepository).save(testPayment);
        }
    }

    @Test
    void updatePayment_PaymentNotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> paymentManagementService.updatePayment("1", testPayment));

        assertEquals(404, exception.getHttpStatus());
        assertEquals("Payment not found", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void updatePayment_PaymentManagementException() {
        when(paymentRepository.findById(1L))
                .thenThrow(new PaymentManagementException(400, "Invalid ID", "FAILURE"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> paymentManagementService.updatePayment("1", testPayment));

        assertEquals(400, exception.getHttpStatus());
        assertEquals("Invalid ID", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void updatePayment_GenericException() {
        when(paymentRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> paymentManagementService.updatePayment("1", testPayment));

        assertEquals(500, exception.getHttpStatus());
        assertTrue(exception.getMessage().contains("Internal Server Error"));
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void generateMonthlyReport_Success() {
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findPaymentsBetween(anyLong(), anyLong())).thenReturn(payments);

        try (MockedStatic<zeta.payments.util.DateUtil> dateUtil = mockStatic(zeta.payments.util.DateUtil.class)) {
            dateUtil.when(() -> zeta.payments.util.DateUtil.convertEpochToDateAndReturnMonth(anyLong()))
                    .thenReturn("January");

            ReportResponse response = paymentManagementService.generateMonthlyReport(1L, 2024L);

            assertNotNull(response);
            assertEquals("MONTHLY", response.getReportType());
            assertEquals("SUCCESS", response.getStatus());
            assertNotNull(response.getReportData());
        }
    }

    @Test
    void generateMonthlyReport_InvalidMonth() {
        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> paymentManagementService.generateMonthlyReport(13L, 2024L));

        assertEquals(404, exception.getHttpStatus());
        assertEquals("Invalid month or year specified", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void generateMonthlyReport_InvalidYear() {
        int futureYear = LocalDate.now().getYear() + 1;
        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> paymentManagementService.generateMonthlyReport(1L, (long) futureYear));

        assertEquals(404, exception.getHttpStatus());
        assertEquals("Invalid month or year specified", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void generateMonthlyReport_NoPayments() {
        when(paymentRepository.findPaymentsBetween(anyLong(), anyLong())).thenReturn(Collections.emptyList());

        ReportResponse response = paymentManagementService.generateMonthlyReport(1L, 2024L);

        assertNotNull(response);
        assertEquals("MONTHLY", response.getReportType());
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("No Payments Found for the specified period", response.getMessage());
    }

    @Test
    void generateQuarterlyReport_Quarter1() {
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findPaymentsBetween(anyLong(), anyLong())).thenReturn(payments);

        try (MockedStatic<zeta.payments.util.DateUtil> dateUtil = mockStatic(zeta.payments.util.DateUtil.class)) {
            dateUtil.when(() -> zeta.payments.util.DateUtil.convertEpochToDateAndReturnMonth(anyLong()))
                    .thenReturn("January");

            ReportResponse response = paymentManagementService.generateQuarterlyReport(1L, 2024L);

            assertNotNull(response);
            assertEquals("QUARTERLY", response.getReportType());
            assertEquals("SUCCESS", response.getStatus());
        }
    }

    @Test
    void generateQuarterlyReport_Quarter2() {
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findPaymentsBetween(anyLong(), anyLong())).thenReturn(payments);

        try (MockedStatic<zeta.payments.util.DateUtil> dateUtil = mockStatic(zeta.payments.util.DateUtil.class)) {
            dateUtil.when(() -> zeta.payments.util.DateUtil.convertEpochToDateAndReturnMonth(anyLong()))
                    .thenReturn("April");

            ReportResponse response = paymentManagementService.generateQuarterlyReport(2L, 2024L);

            assertNotNull(response);
            assertEquals("QUARTERLY", response.getReportType());
        }
    }

    @Test
    void generateQuarterlyReport_Quarter3() {
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findPaymentsBetween(anyLong(), anyLong())).thenReturn(payments);

        try (MockedStatic<zeta.payments.util.DateUtil> dateUtil = mockStatic(zeta.payments.util.DateUtil.class)) {
            dateUtil.when(() -> zeta.payments.util.DateUtil.convertEpochToDateAndReturnMonth(anyLong()))
                    .thenReturn("July");

            ReportResponse response = paymentManagementService.generateQuarterlyReport(3L, 2024L);

            assertNotNull(response);
            assertEquals("QUARTERLY", response.getReportType());
        }
    }

    @Test
    void generateQuarterlyReport_Quarter4() {
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findPaymentsBetween(anyLong(), anyLong())).thenReturn(payments);

        try (MockedStatic<zeta.payments.util.DateUtil> dateUtil = mockStatic(zeta.payments.util.DateUtil.class)) {
            dateUtil.when(() -> zeta.payments.util.DateUtil.convertEpochToDateAndReturnMonth(anyLong()))
                    .thenReturn("October");

            ReportResponse response = paymentManagementService.generateQuarterlyReport(4L, 2024L);

            assertNotNull(response);
            assertEquals("QUARTERLY", response.getReportType());
        }
    }

    @Test
    void generateQuarterlyReport_InvalidQuarter() {
        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> paymentManagementService.generateQuarterlyReport(5L, 2024L));

        assertEquals(404, exception.getHttpStatus());
        assertEquals("Invalid quarter specified", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void getAllPayment_Success() {
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findAll()).thenReturn(payments);

        PaymentLifeCycleManagementResponse response = paymentManagementService.getAllPayment();

        assertNotNull(response);
        assertEquals("Payments Fetched Successfully", response.getMessage());
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(1, response.getPayments().size());
    }

    @Test
    void getAllPayment_Exception() {
        when(paymentRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> paymentManagementService.getAllPayment());

        assertEquals(500, exception.getHttpStatus());
        assertEquals("Internal Server Error", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void getPaymentById_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        PaymentLifeCycleManagementResponse response = paymentManagementService.getPaymentById(1L);

        assertNotNull(response);
        assertEquals("Payment Fetch Successfully", response.getMessage());
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(1, response.getPayments().size());
    }

    @Test
    void getPaymentById_PaymentNotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> paymentManagementService.getPaymentById(1L));

        assertEquals(404, exception.getHttpStatus());
        assertEquals("Payment not found", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void getPaymentById_GenericException() {
        when(paymentRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> paymentManagementService.getPaymentById(1L));

        assertEquals(500, exception.getHttpStatus());
        assertTrue(exception.getMessage().contains("Internal Server Error"));
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void deletePaymentById_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        PaymentLifeCycleManagementResponse response = paymentManagementService.deletePaymentById(1L);

        assertNotNull(response);
        assertEquals("Payment Deleted Successfully", response.getMessage());
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(1, response.getPayments().size());
        verify(paymentRepository).delete(testPayment);
    }

    @Test
    void deletePaymentById_PaymentNotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> paymentManagementService.deletePaymentById(1L));

        assertEquals(404, exception.getHttpStatus());
        assertEquals("Payment not found", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void deletePaymentById_PaymentManagementException() {
        when(paymentRepository.findById(1L))
                .thenThrow(new PaymentManagementException(400, "Invalid ID", "FAILURE"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> paymentManagementService.deletePaymentById(1L));

        assertEquals(400, exception.getHttpStatus());
        assertEquals("Invalid ID", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void deletePaymentById_GenericException() {
        when(paymentRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> paymentManagementService.deletePaymentById(1L));

        assertEquals(500, exception.getHttpStatus());
        assertTrue(exception.getMessage().contains("Internal Server Error"));
        assertEquals("FAILURE", exception.getStatus());
    }

    @Test
    void buildReport_WithIncomingAndOutgoingPayments() {
        Payment incomingPayment = new Payment();
        incomingPayment.setAmount("1000.0");
        incomingPayment.setCurrency("USD");
        incomingPayment.setType(PaymentType.INCOMING);
        incomingPayment.setCreatedAt(System.currentTimeMillis());

        Payment outgoingPayment = new Payment();
        outgoingPayment.setAmount("500.0");
        outgoingPayment.setCurrency("INR");
        outgoingPayment.setType(PaymentType.OUTGOING);
        outgoingPayment.setCreatedAt(System.currentTimeMillis());

        List<Payment> payments = Arrays.asList(incomingPayment, outgoingPayment);
        when(paymentRepository.findPaymentsBetween(anyLong(), anyLong())).thenReturn(payments);

        try (MockedStatic<zeta.payments.util.DateUtil> dateUtil = mockStatic(zeta.payments.util.DateUtil.class)) {
            dateUtil.when(() -> zeta.payments.util.DateUtil.convertEpochToDateAndReturnMonth(anyLong()))
                    .thenReturn("January");

            ReportResponse response = paymentManagementService.generateMonthlyReport(1L, 2024L);

            assertNotNull(response);
            assertEquals("CREDIT", response.getBalanceType()); // More incoming than outgoing
            assertTrue(response.getTotalNetBalance() > 0);
        }
    }

    @Test
    void convertToINR_WithUSDCurrency() {
        // Test currency conversion functionality through report generation
        Payment usdPayment = new Payment();
        usdPayment.setAmount("100.0");
        usdPayment.setCurrency("USD");
        usdPayment.setType(PaymentType.INCOMING);
        usdPayment.setCreatedAt(System.currentTimeMillis());

        List<Payment> payments = Arrays.asList(usdPayment);
        when(paymentRepository.findPaymentsBetween(anyLong(), anyLong())).thenReturn(payments);

        try (MockedStatic<zeta.payments.util.DateUtil> dateUtil = mockStatic(zeta.payments.util.DateUtil.class)) {
            dateUtil.when(() -> zeta.payments.util.DateUtil.convertEpochToDateAndReturnMonth(anyLong()))
                    .thenReturn("January");

            ReportResponse response = paymentManagementService.generateMonthlyReport(1L, 2024L);

            assertNotNull(response);
            // USD 100 * 83 = INR 8300, so total should reflect this conversion
            assertTrue(response.getTotalNetBalance() > 8000);
        }
    }

    @Test
    void convertToINR_WithUnsupportedCurrency() {
        Payment unknownCurrencyPayment = new Payment();
        unknownCurrencyPayment.setAmount("100.0");
        unknownCurrencyPayment.setCurrency("XYZ");
        unknownCurrencyPayment.setType(PaymentType.INCOMING);
        unknownCurrencyPayment.setCreatedAt(System.currentTimeMillis());

        List<Payment> payments = Arrays.asList(unknownCurrencyPayment);
        when(paymentRepository.findPaymentsBetween(anyLong(), anyLong())).thenReturn(payments);

        try (MockedStatic<zeta.payments.util.DateUtil> dateUtil = mockStatic(zeta.payments.util.DateUtil.class)) {
            dateUtil.when(() -> zeta.payments.util.DateUtil.convertEpochToDateAndReturnMonth(anyLong()))
                    .thenReturn("January");

            assertThrows(IllegalArgumentException.class,
                    () -> paymentManagementService.generateMonthlyReport(1L, 2024L));
        }
    }
}
