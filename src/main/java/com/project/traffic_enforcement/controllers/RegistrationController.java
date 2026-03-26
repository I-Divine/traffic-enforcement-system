package com.project.traffic_enforcement.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.traffic_enforcement.dto.OfficerProfileResponse;
import com.project.traffic_enforcement.dto.OfficerUpdateRequest;
import com.project.traffic_enforcement.dto.OwnerLookupResponse;
import com.project.traffic_enforcement.dto.OwnerUpdateRequest;
import com.project.traffic_enforcement.services.RegistrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/registration")
@RequiredArgsConstructor
public class RegistrationController {

    @Autowired
    private final RegistrationService registrationService;

    @PutMapping("/owners/{ownerId}")
    @PreAuthorize("hasAnyRole('ADMIN','REGISTRATION_OFFICERS')")
    public ResponseEntity<OwnerLookupResponse> updateOwner(@PathVariable UUID ownerId,
            @RequestBody OwnerUpdateRequest request) {
        return ResponseEntity.ok(registrationService.updateOwner(ownerId, request));
    }

    @PutMapping("/officers/{officerId}")
    @PreAuthorize("hasAnyRole('ADMIN','REGISTRATION_OFFICERS')")
    public ResponseEntity<OfficerProfileResponse> updateOfficer(@PathVariable UUID officerId,
            @RequestBody OfficerUpdateRequest request) {
        return ResponseEntity.ok(registrationService.updateOfficer(officerId, request));
    }
}
