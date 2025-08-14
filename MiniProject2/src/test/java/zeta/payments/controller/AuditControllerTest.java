package zeta.payments.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import zeta.payments.commons.enums.PaymentCategory;
import zeta.payments.commons.enums.PaymentStatus;
import zeta.payments.commons.enums.PaymentType;
import zeta.payments.dto.response.AuditLifeCycleManagementResponse;
import zeta.payments.entity.Audit;
import zeta.payments.exception.PaymentManagementException;
import zeta.payments.service.impl.AuditTrialManagementServiceImpl;
import zeta.payments.util.JwtUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuditController.class)
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditTrialManagementServiceImpl auditTrialManagementService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Audit testAudit;
    private AuditLifeCycleManagementResponse successResponse;
    private AuditLifeCycleManagementResponse emptyResponse;

    @BeforeEach
    void setUp() {
        Audit.AuditId auditId = new Audit.AuditId(100L, 1L);

        testAudit = new Audit();
        testAudit.setId(auditId);
        testAudit.setRevisionType("CREATE");
        testAudit.setAmount("1000.0");
        testAudit.setCurrency("INR");
        testAudit.setCategory(PaymentCategory.INVOICE);
        testAudit.setType(PaymentType.INCOMING);
        testAudit.setStatus(PaymentStatus.COMPLETED);
        testAudit.setAccountName("Test Account");
        testAudit.setCreatedBy("testuser");

        successResponse = AuditLifeCycleManagementResponse.builder()
                .message("Audit records fetched successfully")
                .audits(Arrays.asList(testAudit))
                .status("success")
                .build();

        emptyResponse = AuditLifeCycleManagementResponse.builder()
                .message("Audit records fetched successfully")
                .audits(Collections.emptyList())
                .status("success")
                .build();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllAudits_Success_WithAdminRole() throws Exception {
        when(auditTrialManagementService.getAllPaymentAudit()).thenReturn(successResponse);

        mockMvc.perform(get("/api/v1/audits")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Audit records fetched successfully"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.audits").isArray())
                .andExpect(jsonPath("$.audits[0].revisionType").value("CREATE"))
                .andExpect(jsonPath("$.audits[0].amount").value("1000.0"))
                .andExpect(jsonPath("$.audits[0].currency").value("INR"))
                .andExpect(jsonPath("$.audits[0].category").value("INVOICE"))
                .andExpect(jsonPath("$.audits[0].type").value("INCOMING"))
                .andExpect(jsonPath("$.audits[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$.audits[0].accountName").value("Test Account"))
                .andExpect(jsonPath("$.audits[0].createdBy").value("testuser"));

        verify(auditTrialManagementService, times(1)).getAllPaymentAudit();
    }

    @Test
    @WithMockUser(roles = {"VIEWER"})
    void getAllAudits_Success_WithViewerRole() throws Exception {
        when(auditTrialManagementService.getAllPaymentAudit()).thenReturn(successResponse);

        mockMvc.perform(get("/api/v1/audits")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Audit records fetched successfully"))
                .andExpect(jsonPath("$.status").value("success"));

        verify(auditTrialManagementService, times(1)).getAllPaymentAudit();
    }

    @Test
    @WithMockUser(roles = {"FINANCE_MANAGER"})
    void getAllAudits_Success_WithFinanceManagerRole() throws Exception {
        when(auditTrialManagementService.getAllPaymentAudit()).thenReturn(successResponse);

        mockMvc.perform(get("/api/v1/audits")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Audit records fetched successfully"))
                .andExpect(jsonPath("$.status").value("success"));

        verify(auditTrialManagementService, times(1)).getAllPaymentAudit();
    }

    @Test
    void getAllAudits_Unauthorized_WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/audits")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(auditTrialManagementService, never()).getAllPaymentAudit();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllAudits_EmptyList() throws Exception {
        when(auditTrialManagementService.getAllPaymentAudit()).thenReturn(emptyResponse);

        mockMvc.perform(get("/api/v1/audits")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Audit records fetched successfully"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.audits").isArray())
                .andExpect(jsonPath("$.audits").isEmpty());

        verify(auditTrialManagementService, times(1)).getAllPaymentAudit();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllAudits_ServiceException() throws Exception {
        when(auditTrialManagementService.getAllPaymentAudit())
                .thenThrow(new PaymentManagementException(500, "Internal Server Error", "FAILURE"));

        mockMvc.perform(get("/api/v1/audits")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(auditTrialManagementService, times(1)).getAllPaymentAudit();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAuditById_Success_WithAdminRole() throws Exception {
        when(auditTrialManagementService.getPaymentAuditById(100L)).thenReturn(successResponse);

        mockMvc.perform(get("/api/v1/audits/100")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Audit records fetched successfully"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.audits").isArray())
                .andExpect(jsonPath("$.audits[0].revisionType").value("CREATE"))
                .andExpect(jsonPath("$.audits[0].amount").value("1000.0"))
                .andExpect(jsonPath("$.audits[0].currency").value("INR"))
                .andExpect(jsonPath("$.audits[0].category").value("INVOICE"))
                .andExpect(jsonPath("$.audits[0].type").value("INCOMING"))
                .andExpect(jsonPath("$.audits[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$.audits[0].accountName").value("Test Account"))
                .andExpect(jsonPath("$.audits[0].createdBy").value("testuser"));

        verify(auditTrialManagementService, times(1)).getPaymentAuditById(100L);
    }

    @Test
    @WithMockUser(roles = {"VIEWER"})
    void getAuditById_Success_WithViewerRole() throws Exception {
        when(auditTrialManagementService.getPaymentAuditById(100L)).thenReturn(successResponse);

        mockMvc.perform(get("/api/v1/audits/100")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Audit records fetched successfully"))
                .andExpect(jsonPath("$.status").value("success"));

        verify(auditTrialManagementService, times(1)).getPaymentAuditById(100L);
    }

    @Test
    @WithMockUser(roles = {"FINANCE_MANAGER"})
    void getAuditById_Success_WithFinanceManagerRole() throws Exception {
        when(auditTrialManagementService.getPaymentAuditById(100L)).thenReturn(successResponse);

        mockMvc.perform(get("/api/v1/audits/100")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Audit records fetched successfully"))
                .andExpect(jsonPath("$.status").value("success"));

        verify(auditTrialManagementService, times(1)).getPaymentAuditById(100L);
    }

    @Test
    void getAuditById_Unauthorized_WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/audits/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(auditTrialManagementService, never()).getPaymentAuditById(100L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAuditById_EmptyList() throws Exception {
        when(auditTrialManagementService.getPaymentAuditById(100L)).thenReturn(emptyResponse);

        mockMvc.perform(get("/api/v1/audits/100")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Audit records fetched successfully"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.audits").isArray())
                .andExpect(jsonPath("$.audits").isEmpty());

        verify(auditTrialManagementService, times(1)).getPaymentAuditById(100L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAuditById_ServiceException() throws Exception {
        when(auditTrialManagementService.getPaymentAuditById(100L))
                .thenThrow(new PaymentManagementException(500, "An error occurred while fetching audit records", "FAILURE"));

        mockMvc.perform(get("/api/v1/audits/100")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(auditTrialManagementService, times(1)).getPaymentAuditById(100L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAuditById_NotFound() throws Exception {
        when(auditTrialManagementService.getPaymentAuditById(999L))
                .thenThrow(new PaymentManagementException(404, "Audit not found", "FAILURE"));

        mockMvc.perform(get("/api/v1/audits/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(auditTrialManagementService, times(1)).getPaymentAuditById(999L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAuditById_InvalidPathVariable() throws Exception {
        mockMvc.perform(get("/api/v1/audits/invalid")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(auditTrialManagementService, never()).getPaymentAuditById(any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAuditById_MultipleAudits() throws Exception {
        Audit.AuditId auditId2 = new Audit.AuditId(100L, 2L);
        Audit audit2 = new Audit();
        audit2.setId(auditId2);
        audit2.setRevisionType("UPDATE");
        audit2.setAmount("1500.0");
        audit2.setCurrency("INR");
        audit2.setCategory(PaymentCategory.VENDOR_SETTLEMENT);
        audit2.setType(PaymentType.INCOMING);
        audit2.setStatus(PaymentStatus.COMPLETED);
        audit2.setAccountName("Test Account");
        audit2.setCreatedBy("testuser2");

        List<Audit> audits = Arrays.asList(testAudit, audit2);
        AuditLifeCycleManagementResponse multipleAuditsResponse = AuditLifeCycleManagementResponse.builder()
                .message("Audit records fetched successfully")
                .audits(audits)
                .status("success")
                .build();

        when(auditTrialManagementService.getPaymentAuditById(100L)).thenReturn(multipleAuditsResponse);

        mockMvc.perform(get("/api/v1/audits/100")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Audit records fetched successfully"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.audits").isArray())
                .andExpect(jsonPath("$.audits.length()").value(2))
                .andExpect(jsonPath("$.audits[0].revisionType").value("CREATE"))
                .andExpect(jsonPath("$.audits[0].amount").value("1000.0"))
                .andExpect(jsonPath("$.audits[1].revisionType").value("UPDATE"))
                .andExpect(jsonPath("$.audits[1].amount").value("1500.0"))
                .andExpect(jsonPath("$.audits[1].createdBy").value("testuser2"));

        verify(auditTrialManagementService, times(1)).getPaymentAuditById(100L);
    }
}