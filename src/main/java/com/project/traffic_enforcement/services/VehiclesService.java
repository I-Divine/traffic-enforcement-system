package com.project.traffic_enforcement.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.traffic_enforcement.dto.VehicleRequest;
import com.project.traffic_enforcement.dto.VehicleResponse;
import com.project.traffic_enforcement.models.Owners;
import com.project.traffic_enforcement.models.Users;
import com.project.traffic_enforcement.models.Vehicles;
import com.project.traffic_enforcement.models.enums.Roles;
import com.project.traffic_enforcement.repository.OwnersRepository;
import com.project.traffic_enforcement.repository.UsersRepository;
import com.project.traffic_enforcement.repository.VehiclesRepository;
import com.project.traffic_enforcement.security.config.UserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehiclesService {

    private final VehiclesRepository vehiclesRepository;
    private final UsersRepository usersRepository;
    private final OwnersRepository ownersRepository;

    public VehicleResponse createVehicle(VehicleRequest request) {
        Owners owner = resolveCurrentOwner();

        Vehicles vehicle = new Vehicles();
        vehicle.setOwner(owner);
        vehicle.setPlateNumber(request.getPlateNumber());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setColor(request.getColor());
        vehicle.setRegistrationDate(request.getRegistrationDate());
        vehicle.setRegistrationExpiry(request.getRegistrationExpiry());

        Vehicles saved = vehiclesRepository.save(vehicle);
        return mapToResponse(saved);
    }

    public List<VehicleResponse> getMyVehicles() {
        Owners owner = resolveCurrentOwner();
        return vehiclesRepository.findByOwner(owner)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Owners resolveCurrentOwner() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String email = resolveEmail(authentication);
        System.out.println(email);
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != Roles.OWNERS) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owners can manage vehicles");
        }

        return ownersRepository.findByOwner(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner profile not found"));
    }

    private String resolveEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return authentication.getName();
    }

    private VehicleResponse mapToResponse(Vehicles vehicle) {
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
