package com.project.traffic_enforcement.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.project.traffic_enforcement.dto.OwnerLookupResponse;
import com.project.traffic_enforcement.services.OwnerLookupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
public class OwnersController {

    private final OwnerLookupService ownerLookupService;

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','ROAD_OFFICERS','APPEAL_OFFICERS')")
    public ResponseEntity<OwnerLookupResponse> searchOwner(
            @RequestParam(required = false) String driversLicenseNumber,
            @RequestParam(required = false) String plateNumber
    ) {
        if (driversLicenseNumber == null && plateNumber == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide driversLicenseNumber or plateNumber");
        }
        if (driversLicenseNumber != null && plateNumber != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide only one search parameter");
        }
        if (driversLicenseNumber != null) {
            return ResponseEntity.ok(ownerLookupService.findByDriversLicenseNumber(driversLicenseNumber));
        }
        return ResponseEntity.ok(ownerLookupService.findByPlateNumber(plateNumber));
    }
}
