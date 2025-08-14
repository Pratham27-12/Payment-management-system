package zeta.payments.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zeta.payments.commons.enums.PaymentCategory;
import zeta.payments.commons.enums.PaymentStatus;
import zeta.payments.commons.enums.PaymentType;
import zeta.payments.dto.response.AuditLifeCycleManagementResponse;
import zeta.payments.entity.Audit;
import zeta.payments.exception.PaymentManagementException;
import zeta.payments.repository.AuditTrialRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditTrialManagementServiceImplTest {

    @Mock
    private AuditTrialRepository auditTrialRepository;

    @InjectMocks
    private AuditTrialManagementServiceImpl auditTrialManagementService;

    private Audit testAudit;
    private Audit.AuditId auditId;

    @BeforeEach
    void setUp() {
        auditId = new Audit.AuditId(100L, 1L);

        testAudit = new Audit();
        testAudit.setId(auditId);
        testAudit.setRevisionType("CREATE");
        testAudit.setAmount("1000.0");
        testAudit.setCurrency("INR");
        testAudit.setCategory(PaymentCategory.VENDOR_SETTLEMENT);
        testAudit.setType(PaymentType.INCOMING);
        testAudit.setStatus(PaymentStatus.COMPLETED);
        testAudit.setAccountName("Test Account");
        testAudit.setCreatedBy("testuser");
        testAudit.setCreatedAt(System.currentTimeMillis());
        testAudit.setUpdatedAt(System.currentTimeMillis());
    }

    @Test
    void getPaymentAuditById_Success() {
        List<Audit> audits = Arrays.asList(testAudit);
        when(auditTrialRepository.findByPaymentId(100L)).thenReturn(audits);

        AuditLifeCycleManagementResponse response = auditTrialManagementService.getPaymentAuditById(100L);

        assertNotNull(response);
        assertEquals("Audit records fetched successfully", response.getMessage());
        assertEquals("success", response.getStatus());
        assertEquals(1, response.getAudits().size());

        Audit returnedAudit = response.getAudits().get(0);
        assertEquals(testAudit.getId().getPaymentId(), returnedAudit.getId().getPaymentId());
        assertEquals(testAudit.getId().getRevisionCount(), returnedAudit.getId().getRevisionCount());
        assertEquals(testAudit.getRevisionType(), returnedAudit.getRevisionType());
        assertEquals(testAudit.getAmount(), returnedAudit.getAmount());
        assertEquals(testAudit.getCurrency(), returnedAudit.getCurrency());
        assertEquals(testAudit.getCategory(), returnedAudit.getCategory());
        assertEquals(testAudit.getType(), returnedAudit.getType());
        assertEquals(testAudit.getStatus(), returnedAudit.getStatus());
        assertEquals(testAudit.getAccountName(), returnedAudit.getAccountName());
        assertEquals(testAudit.getCreatedBy(), returnedAudit.getCreatedBy());

        verify(auditTrialRepository).findByPaymentId(100L);
    }

    @Test
    void getPaymentAuditById_EmptyList() {
        when(auditTrialRepository.findByPaymentId(100L)).thenReturn(Collections.emptyList());

        AuditLifeCycleManagementResponse response = auditTrialManagementService.getPaymentAuditById(100L);

        assertNotNull(response);
        assertEquals("Audit records fetched successfully", response.getMessage());
        assertEquals("success", response.getStatus());
        assertTrue(response.getAudits().isEmpty());
        verify(auditTrialRepository).findByPaymentId(100L);
    }

    @Test
    void getPaymentAuditById_Exception() {
        when(auditTrialRepository.findByPaymentId(100L)).thenThrow(new RuntimeException("Database connection failed"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> auditTrialManagementService.getPaymentAuditById(100L));

        assertEquals(500, exception.getHttpStatus());
        assertEquals("An error occurred while fetching audit records", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
        verify(auditTrialRepository).findByPaymentId(100L);
    }

    @Test
    void getAllPaymentAudit_Success() {
        List<Audit> audits = Arrays.asList(testAudit);
        when(auditTrialRepository.findAll()).thenReturn(audits);

        AuditLifeCycleManagementResponse response = auditTrialManagementService.getAllPaymentAudit();

        assertNotNull(response);
        assertEquals("Audit records fetched successfully", response.getMessage());
        assertEquals("success", response.getStatus());
        assertEquals(1, response.getAudits().size());

        Audit returnedAudit = response.getAudits().get(0);
        assertEquals(testAudit.getId().getPaymentId(), returnedAudit.getId().getPaymentId());
        assertEquals(testAudit.getId().getRevisionCount(), returnedAudit.getId().getRevisionCount());
        assertEquals(testAudit.getRevisionType(), returnedAudit.getRevisionType());

        verify(auditTrialRepository).findAll();
    }

    @Test
    void getAllPaymentAudit_EmptyList() {
        when(auditTrialRepository.findAll()).thenReturn(Collections.emptyList());

        AuditLifeCycleManagementResponse response = auditTrialManagementService.getAllPaymentAudit();

        assertNotNull(response);
        assertEquals("Audit records fetched successfully", response.getMessage());
        assertEquals("success", response.getStatus());
        assertTrue(response.getAudits().isEmpty());
        verify(auditTrialRepository).findAll();
    }

    @Test
    void getAllPaymentAudit_Exception() {
        when(auditTrialRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> auditTrialManagementService.getAllPaymentAudit());

        assertEquals(500, exception.getHttpStatus());
        assertEquals("Internal Server Error", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
        verify(auditTrialRepository).findAll();
    }

    @Test
    void getAllPaymentAudit_MultipleAudits() {
        Audit.AuditId auditId2 = new Audit.AuditId(200L, 1L);
        Audit audit2 = new Audit();
        audit2.setId(auditId2);
        audit2.setRevisionType("UPDATE");
        audit2.setAmount("2000.0");
        audit2.setCurrency("USD");
        audit2.setCategory(PaymentCategory.REFUND);
        audit2.setType(PaymentType.OUTGOING);
        audit2.setStatus(PaymentStatus.PENDING);
        audit2.setAccountName("Business Account");
        audit2.setCreatedBy("testuser2");
        audit2.setCreatedAt(System.currentTimeMillis());
        audit2.setUpdatedAt(System.currentTimeMillis());

        List<Audit> audits = Arrays.asList(testAudit, audit2);
        when(auditTrialRepository.findAll()).thenReturn(audits);

        AuditLifeCycleManagementResponse response = auditTrialManagementService.getAllPaymentAudit();

        assertNotNull(response);
        assertEquals("Audit records fetched successfully", response.getMessage());
        assertEquals("success", response.getStatus());
        assertEquals(2, response.getAudits().size());

        // Verify both audits are present
        boolean foundTestAudit = false;
        boolean foundAudit2 = false;
        for (Audit audit : response.getAudits()) {
            if (audit.getId().getPaymentId().equals(100L)) {
                foundTestAudit = true;
                assertEquals("CREATE", audit.getRevisionType());
                assertEquals("testuser", audit.getCreatedBy());
            } else if (audit.getId().getPaymentId().equals(200L)) {
                foundAudit2 = true;
                assertEquals("UPDATE", audit.getRevisionType());
                assertEquals("testuser2", audit.getCreatedBy());
            }
        }
        assertTrue(foundTestAudit);
        assertTrue(foundAudit2);

        verify(auditTrialRepository).findAll();
    }

    @Test
    void getPaymentAuditById_MultipleAuditsForSamePayment() {
        Audit.AuditId auditId2 = new Audit.AuditId(100L, 2L); // Same payment ID, different revision
        Audit audit2 = new Audit();
        audit2.setId(auditId2);
        audit2.setRevisionType("UPDATE");
        audit2.setAmount("1500.0");
        audit2.setCurrency("INR");
        audit2.setCategory(PaymentCategory.SALARY);
        audit2.setType(PaymentType.INCOMING);
        audit2.setStatus(PaymentStatus.COMPLETED);
        audit2.setAccountName("Test Account");
        audit2.setCreatedBy("testuser2");
        audit2.setCreatedAt(System.currentTimeMillis());
        audit2.setUpdatedAt(System.currentTimeMillis());

        List<Audit> audits = Arrays.asList(testAudit, audit2);
        when(auditTrialRepository.findByPaymentId(100L)).thenReturn(audits);

        AuditLifeCycleManagementResponse response = auditTrialManagementService.getPaymentAuditById(100L);

        assertNotNull(response);
        assertEquals("Audit records fetched successfully", response.getMessage());
        assertEquals("success", response.getStatus());
        assertEquals(2, response.getAudits().size());

        // Verify both audits are for the same payment ID but different revisions
        for (Audit audit : response.getAudits()) {
            assertEquals(100L, audit.getId().getPaymentId());
        }

        // Check that we have both revision 1 and revision 2
        boolean foundRevision1 = false;
        boolean foundRevision2 = false;
        for (Audit audit : response.getAudits()) {
            if (audit.getId().getRevisionCount().equals(1L)) {
                foundRevision1 = true;
                assertEquals("CREATE", audit.getRevisionType());
                assertEquals("1000.0", audit.getAmount());
            } else if (audit.getId().getRevisionCount().equals(2L)) {
                foundRevision2 = true;
                assertEquals("UPDATE", audit.getRevisionType());
                assertEquals("1500.0", audit.getAmount());
            }
        }
        assertTrue(foundRevision1);
        assertTrue(foundRevision2);

        verify(auditTrialRepository).findByPaymentId(100L);
    }

    @Test
    void getPaymentAuditById_NullPaymentId() {
        when(auditTrialRepository.findByPaymentId(null)).thenThrow(new IllegalArgumentException("Payment ID cannot be null"));

        PaymentManagementException exception = assertThrows(PaymentManagementException.class,
                () -> auditTrialManagementService.getPaymentAuditById(null));

        assertEquals(500, exception.getHttpStatus());
        assertEquals("An error occurred while fetching audit records", exception.getMessage());
        assertEquals("FAILURE", exception.getStatus());
        verify(auditTrialRepository).findByPaymentId(null);
    }

    @Test
    void testAuditIdEquality() {
        Audit.AuditId id1 = new Audit.AuditId(100L, 1L);
        Audit.AuditId id2 = new Audit.AuditId(100L, 1L);
        Audit.AuditId id3 = new Audit.AuditId(100L, 2L);

        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}
