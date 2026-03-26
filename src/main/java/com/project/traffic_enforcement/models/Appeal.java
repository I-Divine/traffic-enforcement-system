package com.project.traffic_enforcement.models;

import java.util.UUID;

import com.project.traffic_enforcement.models.enums.AppealStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Appeal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID appealId;

    @ManyToOne
    @JoinColumn(name = "violation_id", nullable = false)
    private Violation violation;

    private String description;
    private String evidenceUrl;

    @Enumerated(EnumType.STRING)
    private AppealStatus status;
}
