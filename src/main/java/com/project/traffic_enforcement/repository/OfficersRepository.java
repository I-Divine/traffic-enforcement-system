package com.project.traffic_enforcement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.traffic_enforcement.models.Officers;
import com.project.traffic_enforcement.models.Users;

@Repository
public interface OfficersRepository extends JpaRepository<Officers, UUID> {
    Optional<Officers> findByBadgeNumber(String badgeNumber);
    Optional<Officers> findByUsers(Users users);
}
