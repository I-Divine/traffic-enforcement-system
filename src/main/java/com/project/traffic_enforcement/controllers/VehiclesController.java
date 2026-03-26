package com.project.traffic_enforcement.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.traffic_enforcement.dto.VehicleRegistrationRequest;
import com.project.traffic_enforcement.dto.VehicleRequest;
import com.project.traffic_enforcement.dto.VehicleResponse;
import com.project.traffic_enforcement.dto.VehicleUpdateRequest;
import com.project.traffic_enforcement.services.VehiclesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehiclesController {

    private final VehiclesService vehiclesService;

    @PostMapping
    @PreAuthorize("hasRole('OWNERS')")
    public ResponseEntity<VehicleResponse> createVehicle(@RequestBody VehicleRequest request) {
        return ResponseEntity.ok(vehiclesService.createVehicle(request));
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN','REGISTRATION_OFFICERS')")
    public ResponseEntity<VehicleResponse> registerVehicle(@RequestBody VehicleRegistrationRequest request) {
        return ResponseEntity.ok(vehiclesService.registerVehicle(request));
    }

    @PutMapping("/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN','REGISTRATION_OFFICERS')")
    public ResponseEntity<VehicleResponse> updateVehicle(@PathVariable UUID vehicleId,
            @RequestBody VehicleUpdateRequest request) {
        return ResponseEntity.ok(vehiclesService.updateVehicle(vehicleId, request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('OWNERS')")
    public ResponseEntity<List<VehicleResponse>> getMyVehicles() {
        return ResponseEntity.ok(vehiclesService.getMyVehicles());
    }
}
