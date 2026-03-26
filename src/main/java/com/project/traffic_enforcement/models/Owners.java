package com.project.traffic_enforcement.models;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Entity
@Data
public class Owners {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID ownerId;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users owner;
    @OneToMany(mappedBy = "owner")
    private List<Vehicles> vehicles;
    private String address;
    private String city;
    private String state;
    private String driversLicenseNumber;
}
