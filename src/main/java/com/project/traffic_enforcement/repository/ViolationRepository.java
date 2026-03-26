package com.project.traffic_enforcement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.traffic_enforcement.models.Vehicles;
import com.project.traffic_enforcement.models.Violation;
import com.project.traffic_enforcement.models.Officers;
import com.project.traffic_enforcement.models.enums.ViolationStatus;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, UUID> {
    List<Violation> findByVehicle(Vehicles vehicle);
    List<Violation> findByOfficer(Officers officer);
    List<Violation> findByPlateNumber(String plateNumber);
    List<Violation> findByVehicleIn(List<Vehicles> vehicles);
    List<Violation> findByStatus(ViolationStatus status);
    
    @Query("SELECT v FROM Violation v WHERE v.status = 'UNRESOLVED'")
    List<Violation> findAllUnresolvedViolations();
    
    @Query("SELECT v FROM Violation v WHERE v.vehicle.owner.owner.userId = :userId AND v.status != 'RESOLVED'")
    List<Violation> findUserUnresolvedViolations(@Param("userId") UUID userId);
}
