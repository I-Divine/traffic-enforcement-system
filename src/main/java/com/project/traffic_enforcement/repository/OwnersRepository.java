package com.project.traffic_enforcement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.traffic_enforcement.models.Owners;
import com.project.traffic_enforcement.models.Users;

@Repository
public interface OwnersRepository extends JpaRepository<Owners, UUID> {
    Optional<Owners> findByDriversLicenseNumber(String driversLicenseNumber);
    Optional<Owners> findByOwner(Users owner);
}
