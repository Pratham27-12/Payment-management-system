package org.example.repository;

import org.example.model.dto.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {


    @Modifying
    @Transactional
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDateEpoch AND :endDateEpoch")
    List<Payment> findPaymentsBetween(@Param("startDateEpoch")Long startDateEpoch,
                                                         @Param("endDateEpoch")Long endDateEpoch);
}
