package com.project.traffic_enforcement.dto;

import lombok.Data;

@Data
public class OfficerDetailsResponse {
    private String badgeNumber;
    private String department;
    private String rank;
    private String assignmentArea;
}
