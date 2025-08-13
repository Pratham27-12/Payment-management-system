package zeta.payments.repository;

import jakarta.websocket.server.PathParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import zeta.payments.entity.Audit;

import java.util.List;

public interface AuditTrialRepository extends JpaRepository<Audit, Long> {
    @Modifying
    @Transactional
    @Query("SELECT a FROM Audit a WHERE a.id = :paymentId")
    List<Audit> findByPaymentId(@PathParam("paymentId") Long paymentId);
}
