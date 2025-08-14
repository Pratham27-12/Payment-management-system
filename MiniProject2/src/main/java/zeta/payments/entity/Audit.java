package zeta.payments.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zeta.payments.commons.enums.PaymentCategory;
import zeta.payments.commons.enums.PaymentStatus;
import zeta.payments.commons.enums.PaymentType;

import java.io.Serializable;

@Entity
@Table(name = "audit_trail", schema = "payment_system")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Audit {
    @EmbeddedId
    private AuditId id;

    @Column(name = "revision_type")
    private String revisionType;

    @Column(name = "amount")
    private String amount;

    @Column(name = "currency")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private PaymentCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Long createdAt;

    @Column(name = "updated_at", updatable = false, insertable = false)
    private Long updatedAt;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuditId implements Serializable {
        @Column(name = "payment_id")
        private Long paymentId;

        @Column(name = "revision_count")
        private Long revisionCount;
    }
}
