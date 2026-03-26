package com.project.traffic_enforcement.dto;

import java.util.UUID;

import com.project.traffic_enforcement.models.enums.Roles;

import lombok.Data;

@Data
public class UserSummaryResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;
    private Roles role;
}
