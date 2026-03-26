package com.project.traffic_enforcement.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.traffic_enforcement.dto.ViolationRequest;
import com.project.traffic_enforcement.dto.ViolationResponse;
import com.project.traffic_enforcement.dto.ViolationTypeFineResponse;
import com.project.traffic_enforcement.services.ViolationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/violations")
@RequiredArgsConstructor
public class ViolationController {

    private final ViolationService violationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROAD_OFFICERS','APPEAL_OFFICERS')")
    public ResponseEntity<ViolationResponse> createViolation(@RequestBody ViolationRequest request) {
        return ResponseEntity.ok(violationService.createViolation(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROAD_OFFICERS','APPEAL_OFFICERS')")
    public ResponseEntity<List<ViolationResponse>> getMyIssuedViolations() {
        return ResponseEntity.ok(violationService.getMyIssuedViolations());
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNERS')")
    public ResponseEntity<List<ViolationResponse>> getMyViolations() {
        return ResponseEntity.ok(violationService.getMyViolations());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','ROAD_OFFICERS','APPEAL_OFFICERS')")
    public ResponseEntity<List<ViolationResponse>> searchByPlate(@RequestParam String plateNumber) {
        return ResponseEntity.ok(violationService.findByPlateNumber(plateNumber));
    }

    @GetMapping("/types")
    @PreAuthorize("hasAnyRole('ADMIN','ROAD_OFFICERS','APPEAL_OFFICERS','OWNERS')")
    public ResponseEntity<List<ViolationTypeFineResponse>> getViolationTypes() {
        return ResponseEntity.ok(violationService.getAvailableViolationTypesWithFines());
    }
}
