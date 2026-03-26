package com.project.traffic_enforcement.models;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    @Column(nullable = false, unique = true)
    private String referenceId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "violation_id", nullable = false)
    private Violation violation;

    @Column(nullable = false)
    private Float amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
