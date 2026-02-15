package com.project.traffic_enforcement.dto;

import java.util.Date;
import java.util.UUID;

import com.project.traffic_enforcement.models.enums.Roles;

import lombok.Data;

@Data
public class UserProfileResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Roles role;
    private Date lastLogin;
    private Date createdAt;
    private OwnerDetailsResponse ownerDetails;
    private OfficerDetailsResponse officerDetails;
}
