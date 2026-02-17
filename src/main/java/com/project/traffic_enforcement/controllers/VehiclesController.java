package com.project.traffic_enforcement.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.project.traffic_enforcement.dto.VehicleRequest;
import com.project.traffic_enforcement.dto.VehicleResponse;
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

    @GetMapping("/me")
    @PreAuthorize("hasRole('OWNERS')")
    public ResponseEntity<List<VehicleResponse>> getMyVehicles() {
        return ResponseEntity.ok(vehiclesService.getMyVehicles());
    }
}
