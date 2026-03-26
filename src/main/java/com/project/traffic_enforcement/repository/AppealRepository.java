package com.project.traffic_enforcement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.traffic_enforcement.models.Appeal;
import com.project.traffic_enforcement.models.enums.AppealStatus;

@Repository
public interface AppealRepository extends JpaRepository<Appeal, UUID> {
    List<Appeal> findByStatus(AppealStatus status);
    
    @Query("SELECT a FROM Appeal a WHERE a.status = 'PENDING'")
    List<Appeal> findAllPendingAppeals();
    
    @Query("SELECT a FROM Appeal a WHERE a.violation.vehicle.owner.owner.userId = :userId AND a.status = 'PENDING'")
    List<Appeal> findUserPendingAppeals(@Param("userId") UUID userId);
}
