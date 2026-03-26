package com.project.traffic_enforcement.dto;

import lombok.Data;

@Data
public class OfficerUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;
    private String badgeNumber;
    private String department;
    private String rank;
    private String assignmentArea;
}
