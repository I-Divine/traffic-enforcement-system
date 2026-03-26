package com.project.traffic_enforcement.dto;

import com.project.traffic_enforcement.models.enums.ViolationType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViolationTypeFineResponse {
    private ViolationType type;
    private Float fineAmount;
}
