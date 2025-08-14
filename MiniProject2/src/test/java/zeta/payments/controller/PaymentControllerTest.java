package zeta.payments.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import zeta.payments.commons.enums.PaymentCategory;
import zeta.payments.commons.enums.PaymentStatus;
import zeta.payments.commons.enums.PaymentType;
import zeta.payments.config.SecurityConfig;
import zeta.payments.dto.response.PaymentLifeCycleManagementResponse;
import zeta.payments.dto.response.ReportResponse;
import zeta.payments.entity.Payment;
import zeta.payments.exception.PaymentManagementException;
import zeta.payments.service.impl.PaymentManagementServiceImpl;
import zeta.payments.util.JwtUtil;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class)
@TestPropertySource(properties = {
        "spring.security.enabled=true"
})
@Import(SecurityConfig.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentManagementServiceImpl paymentManagementService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Payment testPayment;
    private PaymentLifeCycleManagementResponse successResponse;
    private ReportResponse reportResponse;

    @BeforeEach
    void setUp() {
        testPayment = new Payment();
        testPayment.setId(1l);
        testPayment.setAmount("1000.0");
        testPayment.setCurrency("INR");
        testPayment.setCategory(PaymentCategory.REFUND);
        testPayment.setType(PaymentType.INCOMING);
        testPayment.setStatus(PaymentStatus.COMPLETED);
        testPayment.setAccountName("Test Account");
        testPayment.setCreatedBy("testuser");
        testPayment.setCreatedAt(System.currentTimeMillis());

        successResponse = PaymentLifeCycleManagementResponse.builder()
                .payments(Arrays.asList(testPayment))
                .message("Operation successful")
                .status("SUCCESS")
                .build();

        Map<String, ReportResponse.Data> reportData = new HashMap<>();
        ReportResponse.Data data = new ReportResponse.Data();
        data.inComingPayments = 1000.0;
        data.outGoingPayments = 500.0;
        data.netBalance = 500.0;
        reportData.put("January 2024", data);

        reportResponse = ReportResponse.builder()
                .reportType("MONTHLY")
                .date(LocalDate.now())
                .balanceType("CREDIT")
                .reportData(reportData)
                .totalNetBalance(500.0)
                .status("SUCCESS")
                .build();
    }

    // GET /api/v1/payments/{id} Tests
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getPaymentById_Success_WithAdminRole() throws Exception {
        when(paymentManagementService.getPaymentById(1L)).thenReturn(successResponse);

        mockMvc.perform(get("/api/v1/payments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.payments").isArray())
                .andExpect(jsonPath("$.payments[0].id").value(1L))
                .andExpect(jsonPath("$.payments[0].amount").value("1000.0"))
                .andExpect(jsonPath("$.payments[0].currency").value("INR"))
                .andExpect(jsonPath("$.payments[0].category").value("REFUND"))
                .andExpect(jsonPath("$.payments[0].type").value("INCOMING"))
                .andExpect(jsonPath("$.payments[0].status").value("COMPLETED"));

        verify(paymentManagementService, times(1)).getPaymentById(1L);
    }

    @Test
    @WithMockUser(roles = {"VIEWER"})
    void getPaymentById_Success_WithViewerRole() throws Exception {
        when(paymentManagementService.getPaymentById(1L)).thenReturn(successResponse);

        mockMvc.perform(get("/api/v1/payments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentManagementService, times(1)).getPaymentById(1L);
    }

    @Test
    @WithMockUser(roles = {"FINANCE_MANAGER"})
    void getPaymentById_Success_WithFinanceManagerRole() throws Exception {
        when(paymentManagementService.getPaymentById(1L)).thenReturn(successResponse);

        mockMvc.perform(get("/api/v1/payments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentManagementService, times(1)).getPaymentById(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getPaymentById_NotFound() throws Exception {
        when(paymentManagementService.getPaymentById(999L))
                .thenThrow(new PaymentManagementException(404, "Payment not found", "FAILURE"));

        mockMvc.perform(get("/api/v1/payments/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(paymentManagementService, times(1)).getPaymentById(999L);
    }

    // GET /api/v1/payments Tests
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllPayments_Success_WithAdminRole() throws Exception {
        when(paymentManagementService.getAllPayment()).thenReturn(successResponse);

        mockMvc.perform(get("/api/v1/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.payments").isArray())
                .andExpect(jsonPath("$.payments[0].id").value(1L));

        verify(paymentManagementService, times(1)).getAllPayment();
    }

    @Test
    @WithMockUser(roles = {"VIEWER"})
    void getAllPayments_Success_WithViewerRole() throws Exception {
        when(paymentManagementService.getAllPayment()).thenReturn(successResponse);

        mockMvc.perform(get("/api/v1/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentManagementService, times(1)).getAllPayment();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllPayments_EmptyList() throws Exception {
        PaymentLifeCycleManagementResponse emptyResponse = PaymentLifeCycleManagementResponse.builder()
                .payments(Collections.emptyList())
                .message("No payments found")
                .status("SUCCESS")
                .build();

        when(paymentManagementService.getAllPayment()).thenReturn(emptyResponse);

        mockMvc.perform(get("/api/v1/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments").isArray())
                .andExpect(jsonPath("$.payments").isEmpty());

        verify(paymentManagementService, times(1)).getAllPayment();
    }

    // POST /api/v1/payments Tests
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createPaymentRecord_Success_WithAdminRole() throws Exception {
        when(paymentManagementService.createPaymentRecord(any(Payment.class))).thenReturn(successResponse);

        mockMvc.perform(post("/api/v1/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPayment)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.payments[0].id").value(1L));

        verify(paymentManagementService, times(1)).createPaymentRecord(any(Payment.class));
    }

    @Test
    @WithMockUser(roles = {"FINANCE_MANAGER"})
    void createPaymentRecord_Success_WithFinanceManagerRole() throws Exception {
        when(paymentManagementService.createPaymentRecord(any(Payment.class))).thenReturn(successResponse);

        mockMvc.perform(post("/api/v1/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPayment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentManagementService, times(1)).createPaymentRecord(any(Payment.class));
    }

    @Test
    @WithMockUser(roles = {"VIEWER"})
    void createPaymentRecord_Forbidden_WithViewerRole() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPayment)))
                .andExpect(status().isForbidden());

        verify(paymentManagementService, never()).createPaymentRecord(any(Payment.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createPaymentRecord_BadRequest_InvalidPayload() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());

        verify(paymentManagementService, never()).createPaymentRecord(any(Payment.class));
    }

    // PUT /api/v1/payments/{id} Tests
    @Test
    @WithMockUser(roles = {"FINANCE_MANAGER"})
    void updatePaymentRecord_Success() throws Exception {
        when(paymentManagementService.updatePayment(eq("PAY_001"), any(Payment.class))).thenReturn(successResponse);

        mockMvc.perform(put("/api/v1/payments/PAY_001")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPayment)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentManagementService, times(1)).updatePayment(eq("PAY_001"), any(Payment.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updatePaymentRecord_Forbidden_WithAdminRole() throws Exception {
        mockMvc.perform(put("/api/v1/payments/1L")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPayment)))
                .andExpect(status().isForbidden());

        verify(paymentManagementService, never()).updatePayment(anyString(), any(Payment.class));
    }

    @Test
    @WithMockUser(roles = {"FINANCE_MANAGER"})
    void updatePaymentRecord_NotFound() throws Exception {
        when(paymentManagementService.updatePayment(eq("PAY_999"), any(Payment.class)))
                .thenThrow(new PaymentManagementException(404, "Payment not found", "FAILURE"));

        mockMvc.perform(put("/api/v1/payments/PAY_999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPayment)))
                .andExpect(status().isNotFound());

        verify(paymentManagementService, times(1)).updatePayment(eq("PAY_999"), any(Payment.class));
    }

    // GET /api/v1/reports/monthly/{month}/{year} Tests
    @Test
    @WithMockUser(roles = {"FINANCE_MANAGER"})
    void getMonthlyReport_Success() throws Exception {
        when(paymentManagementService.generateMonthlyReport(1L, 2024L)).thenReturn(reportResponse);

        mockMvc.perform(get("/api/v1/reports/month/1/year/2024")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reportType").value("MONTHLY"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.balanceType").value("CREDIT"))
                .andExpect(jsonPath("$.totalNetBalance").value(500.0));

        verify(paymentManagementService, times(1)).generateMonthlyReport(1L, 2024L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getMonthlyReport_Forbidden_WithAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/reports/month/1/year/2024")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(paymentManagementService, never()).generateMonthlyReport(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = {"FINANCE_MANAGER"})
    void getMonthlyReport_InvalidMonth() throws Exception {
        when(paymentManagementService.generateMonthlyReport(13L, 2024L))
                .thenThrow(new PaymentManagementException(400, "Invalid month", "FAILURE"));

        mockMvc.perform(get("/api/v1/reports/month/13/year/2024")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(paymentManagementService, times(1)).generateMonthlyReport(13L, 2024L);
    }

    // GET /api/v1/reports/quarterly/{quarter}/{year} Tests
    @Test
    @WithMockUser(roles = {"FINANCE_MANAGER"})
    void getQuarterlyReport_Success() throws Exception {
        ReportResponse quarterlyResponse = ReportResponse.builder()
                .reportType("QUARTERLY")
                .date(LocalDate.now())
                .balanceType("CREDIT")
                .reportData(new HashMap<>())
                .totalNetBalance(1500.0)
                .status("SUCCESS")
                .build();

        when(paymentManagementService.generateQuarterlyReport(1L, 2024L)).thenReturn(quarterlyResponse);

        mockMvc.perform(get("/api/v1/reports/quarter/1/year/2024")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reportType").value("QUARTERLY"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.totalNetBalance").value(1500.0));

        verify(paymentManagementService, times(1)).generateQuarterlyReport(1L, 2024L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getQuarterlyReport_Forbidden_WithAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/reports/quarter/1/year/2024")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(paymentManagementService, never()).generateQuarterlyReport(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = {"FINANCE_MANAGER"})
    void getQuarterlyReport_InvalidQuarter() throws Exception {
        when(paymentManagementService.generateQuarterlyReport(5L, 2024L))
                .thenThrow(new PaymentManagementException(400, "Invalid quarter", "FAILURE"));

        mockMvc.perform(get("/api/v1/reports/quarter/5/year/2024")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(paymentManagementService, times(1)).generateQuarterlyReport(5L, 2024L);
    }

    // DELETE /api/v1/payments/{id} Tests
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deletePaymentById_Success() throws Exception {
        PaymentLifeCycleManagementResponse deleteResponse = PaymentLifeCycleManagementResponse.builder()
                .payments(Arrays.asList(testPayment))
                .message("Payment Deleted Successfully")
                .status("SUCCESS")
                .build();

        when(paymentManagementService.deletePaymentById(1L)).thenReturn(deleteResponse);

        mockMvc.perform(delete("/api/v1/payments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Payment Deleted Successfully"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentManagementService, times(1)).deletePaymentById(1L);
    }

    @Test
    @WithMockUser(roles = {"FINANCE_MANAGER"})
    void deletePaymentById_Forbidden_WithFinanceManagerRole() throws Exception {
        mockMvc.perform(delete("/api/v1/payments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(paymentManagementService, never()).deletePaymentById(anyLong());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deletePaymentById_NotFound() throws Exception {
        when(paymentManagementService.deletePaymentById(999L))
                .thenThrow(new PaymentManagementException(404, "Payment not found", "FAILURE"));

        mockMvc.perform(delete("/api/v1/payments/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(paymentManagementService, times(1)).deletePaymentById(999L);
    }

    @Test
    void getAllPayments_Unauthorized_WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(paymentManagementService, never()).getAllPayment();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createPaymentRecord_ServiceException() throws Exception {
        when(paymentManagementService.createPaymentRecord(any(Payment.class)))
                .thenThrow(new PaymentManagementException(500, "Internal Server Error", "FAILURE"));

        mockMvc.perform(post("/api/v1/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPayment)))
                .andExpect(status().isInternalServerError());

        verify(paymentManagementService, times(1)).createPaymentRecord(any(Payment.class));
    }
}