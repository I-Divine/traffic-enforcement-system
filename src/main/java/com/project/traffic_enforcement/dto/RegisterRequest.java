package com.project.traffic_enforcement.dto;

import com.project.traffic_enforcement.models.enums.Roles;

import lombok.Data;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private Roles role;
    private OfficerDetailsRequest officerDetails;
    private OwnerDetailsRequest ownerDetails;
}
