package com.project.traffic_enforcement.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.traffic_enforcement.dto.OwnerDetailsResponse;
import com.project.traffic_enforcement.dto.OwnerLookupResponse;
import com.project.traffic_enforcement.dto.VehicleResponse;
import com.project.traffic_enforcement.models.Owners;
import com.project.traffic_enforcement.models.Vehicles;
import com.project.traffic_enforcement.repository.OwnersRepository;
import com.project.traffic_enforcement.repository.VehiclesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerLookupService {

    private final OwnersRepository ownersRepository;
    private final VehiclesRepository vehiclesRepository;

    public OwnerLookupResponse findByDriversLicenseNumber(String driversLicenseNumber) {
        Owners owner = ownersRepository.findByDriversLicenseNumber(driversLicenseNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        return mapOwner(owner);
    }

    public OwnerLookupResponse findByPlateNumber(String plateNumber) {
        Vehicles vehicle = vehiclesRepository.findByPlateNumber(plateNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));
        return mapOwner(vehicle.getOwner());
    }

    private OwnerLookupResponse mapOwner(Owners owner) {
        OwnerLookupResponse response = new OwnerLookupResponse();
        response.setUserId(owner.getOwner().getUserId());
        response.setFirstName(owner.getOwner().getFirstName());
        response.setLastName(owner.getOwner().getLastName());
        response.setEmail(owner.getOwner().getEmail());
        response.setPhoneNumber(owner.getOwner().getPhoneNumber());
        response.setRole(owner.getOwner().getRole());
        response.setOwnerDetails(mapOwnerDetails(owner));
        response.setVehicles(mapVehicles(vehiclesRepository.findByOwner(owner)));
        return response;
    }

    private OwnerDetailsResponse mapOwnerDetails(Owners owner) {
        OwnerDetailsResponse details = new OwnerDetailsResponse();
        details.setAddress(owner.getAddress());
        details.setCity(owner.getCity());
        details.setState(owner.getState());
        details.setDriversLicenseNumber(owner.getDriversLicenseNumber());
        return details;
    }

    private List<VehicleResponse> mapVehicles(List<Vehicles> vehicles) {
        return vehicles.stream()
                .map(this::mapVehicle)
                .collect(Collectors.toList());
    }

    private VehicleResponse mapVehicle(Vehicles vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setVehicleId(vehicle.getVehicleId());
        response.setPlateNumber(vehicle.getPlateNumber());
        response.setMake(vehicle.getMake());
        response.setModel(vehicle.getModel());
        response.setYear(vehicle.getYear());
        response.setColor(vehicle.getColor());
        response.setRegistrationDate(vehicle.getRegistrationDate());
        response.setRegistrationExpiry(vehicle.getRegistrationExpiry());
        return response;
    }
}
