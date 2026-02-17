package com.project.traffic_enforcement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.traffic_enforcement.models.Owners;
import com.project.traffic_enforcement.models.Vehicles;

@Repository
public interface VehiclesRepository extends JpaRepository<Vehicles, UUID> {
    List<Vehicles> findByOwner(Owners owner);
    Optional<Vehicles> findByPlateNumber(String plateNumber);
}
