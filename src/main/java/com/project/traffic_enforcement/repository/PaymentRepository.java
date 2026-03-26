package com.project.traffic_enforcement.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.traffic_enforcement.models.Payment;
import com.project.traffic_enforcement.models.Violation;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.createdAt >= :start and p.createdAt < :end")
    Double sumAmountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    Optional<Payment> findByViolation(Violation violation);
    
    @Query("SELECT v FROM Violation v WHERE v.status != 'PAID' AND v.status != 'DISMISSED' AND v.status != 'RESOLVED' AND NOT EXISTS (SELECT 1 FROM Payment p WHERE p.violation = v)")
    List<Violation> findUnpaidViolations();
    
    @Query("SELECT v FROM Violation v WHERE v.vehicle.owner.owner.userId = :userId AND v.status != 'PAID' AND v.status != 'DISMISSED' AND v.status != 'RESOLVED' AND NOT EXISTS (SELECT 1 FROM Payment p WHERE p.violation = v)")
    List<Violation> findUserUnpaidViolations(@Param("userId") UUID userId);
}
