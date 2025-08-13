package zeta.payments.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import zeta.payments.commons.enums.PaymentCategory;
import zeta.payments.commons.enums.PaymentStatus;
import zeta.payments.commons.enums.PaymentType;


@Entity
@Table(name = "payment_details", schema = "payment_system")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
}
