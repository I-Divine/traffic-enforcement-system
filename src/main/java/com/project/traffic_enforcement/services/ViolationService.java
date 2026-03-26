package com.project.traffic_enforcement.services;

import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.EnumMap;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.traffic_enforcement.dto.ViolationRequest;
import com.project.traffic_enforcement.dto.ViolationResponse;
import com.project.traffic_enforcement.dto.ViolationTypeFineResponse;
import com.project.traffic_enforcement.models.Officers;
import com.project.traffic_enforcement.models.Owners;
import com.project.traffic_enforcement.models.Users;
import com.project.traffic_enforcement.models.Vehicles;
import com.project.traffic_enforcement.models.Violation;
import com.project.traffic_enforcement.models.enums.Roles;
import com.project.traffic_enforcement.models.enums.ViolationStatus;
import com.project.traffic_enforcement.models.enums.ViolationType;
import com.project.traffic_enforcement.repository.OfficersRepository;
import com.project.traffic_enforcement.repository.OwnersRepository;
import com.project.traffic_enforcement.repository.UsersRepository;
import com.project.traffic_enforcement.repository.VehiclesRepository;
import com.project.traffic_enforcement.repository.ViolationRepository;
import com.project.traffic_enforcement.security.config.UserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViolationService {

    private final ViolationRepository violationRepository;
    private final VehiclesRepository vehiclesRepository;
    private final UsersRepository usersRepository;
    private final OfficersRepository officersRepository;
    private final OwnersRepository ownersRepository;

    public ViolationResponse createViolation(ViolationRequest request) {
        Officers officer = resolveCurrentOfficer();

        Vehicles vehicle = vehiclesRepository.findByPlateNumber(request.getPlateNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        Violation violation = new Violation();
        violation.setOfficer(officer);
        violation.setVehicle(vehicle);
        violation.setPlateNumber(request.getPlateNumber());
        violation.setViolationDate(request.getViolationDate());
        violation.setGpsCoordinates(request.getGpsCoordinates());
        violation.setFineAmount(request.getFineAmount());
        violation.setViolationType(request.getViolationType());
        violation.setDescription(request.getDescription());
        violation.setStatus(request.getStatus() != null ? request.getStatus() : ViolationStatus.UNRESOLVED);
        violation.setState(request.getState());
        violation.setLga(request.getLga());

        Violation saved = violationRepository.save(violation);
        return mapToResponse(saved);
    }

    public List<ViolationResponse> getMyIssuedViolations() {
        Officers officer = resolveCurrentOfficer();
        return violationRepository.findByOfficer(officer)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ViolationResponse> getMyViolations() {
        Owners owner = resolveCurrentOwner();
        List<Vehicles> vehicles = vehiclesRepository.findByOwner(owner);
        return violationRepository.findByVehicleIn(vehicles)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ViolationResponse> findByPlateNumber(String plateNumber) {
        return violationRepository.findByPlateNumber(plateNumber)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ViolationType> getAvailableViolationTypes() {
        return Arrays.asList(ViolationType.values());
    }

    public List<ViolationTypeFineResponse> getAvailableViolationTypesWithFines() {
        Map<ViolationType, Float> fineSchedule = new EnumMap<>(ViolationType.class);
        fineSchedule.put(ViolationType.SPEEDING, 20000f);
        fineSchedule.put(ViolationType.RED_LIGHT, 25000f);
        fineSchedule.put(ViolationType.NO_SEAT_BELT, 10000f);
        fineSchedule.put(ViolationType.PHONE_USE_WHILE_DRIVING, 15000f);
        fineSchedule.put(ViolationType.DRUNK_DRIVING, 50000f);
        fineSchedule.put(ViolationType.RECKLESS_DRIVING, 40000f);
        fineSchedule.put(ViolationType.INVALID_LICENSE, 30000f);
        fineSchedule.put(ViolationType.EXPIRED_VEHICLE_DOCUMENT, 20000f);
        fineSchedule.put(ViolationType.WRONG_PARKING, 5000f);
        fineSchedule.put(ViolationType.LANE_VIOLATION, 12000f);
        fineSchedule.put(ViolationType.OVERLOADING, 22000f);
        fineSchedule.put(ViolationType.OTHER, 10000f);

        return Arrays.stream(ViolationType.values())
                .map(type -> new ViolationTypeFineResponse(type, fineSchedule.get(type)))
                .collect(Collectors.toList());
    }

    private Officers resolveCurrentOfficer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String email = resolveEmail(authentication);
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != Roles.ROAD_OFFICERS && user.getRole() != Roles.APPEAL_OFFICERS && user.getRole() != Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only officers or admin can perform this action");
        }

        return officersRepository.findByUsers(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Officer profile not found"));
    }

    private Owners resolveCurrentOwner() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String email = resolveEmail(authentication);
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != Roles.OWNERS) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owners can view their violations");
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

    private ViolationResponse mapToResponse(Violation violation) {
        ViolationResponse resp = new ViolationResponse();
        resp.setViolationId(violation.getViolationId());
        if (violation.getVehicle() != null) {
            resp.setVehicleId(violation.getVehicle().getVehicleId());
        }
        if (violation.getOfficer() != null) {
            resp.setOfficerId(violation.getOfficer().getOfficerId());
        }
        resp.setPlateNumber(violation.getPlateNumber());
        resp.setViolationDate(violation.getViolationDate());
        resp.setGpsCoordinates(violation.getGpsCoordinates());
        resp.setFineAmount(violation.getFineAmount());
        resp.setViolationType(violation.getViolationType());
        resp.setDescription(violation.getDescription());
        resp.setStatus(violation.getStatus());
        resp.setState(violation.getState());
        resp.setLga(violation.getLga());
        return resp;
    }
}
