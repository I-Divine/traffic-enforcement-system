package com.project.traffic_enforcement.models;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Entity
@Data
public class Officers {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID officerId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users users;

    @Column(unique = true, nullable = false)
    private String badgeNumber;
    private String department;
    private String rank;
    private String assignmentArea;
    @OneToMany(mappedBy = "officer")
    private List<Violation> violations;
}
