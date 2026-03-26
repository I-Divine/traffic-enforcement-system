package com.project.traffic_enforcement.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.traffic_enforcement.dto.OfficerDetailsResponse;
import com.project.traffic_enforcement.dto.OfficerProfileResponse;
import com.project.traffic_enforcement.dto.OfficerUpdateRequest;
import com.project.traffic_enforcement.dto.OwnerDetailsResponse;
import com.project.traffic_enforcement.dto.OwnerLookupResponse;
import com.project.traffic_enforcement.dto.OwnerUpdateRequest;
import com.project.traffic_enforcement.dto.VehicleResponse;
import com.project.traffic_enforcement.models.Officers;
import com.project.traffic_enforcement.models.Owners;
import com.project.traffic_enforcement.models.Users;
import com.project.traffic_enforcement.models.Vehicles;
import com.project.traffic_enforcement.repository.OfficersRepository;
import com.project.traffic_enforcement.repository.OwnersRepository;
import com.project.traffic_enforcement.repository.UsersRepository;
import com.project.traffic_enforcement.repository.VehiclesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UsersRepository usersRepository;
    private final OwnersRepository ownersRepository;
    private final OfficersRepository officersRepository;
    private final VehiclesRepository vehiclesRepository;

    public OwnerLookupResponse updateOwner(UUID ownerId, OwnerUpdateRequest request) {
        if (ownerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner ID is required");
        }

        Owners owner = ownersRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));

        Users user = owner.getOwner();
        updateUserFields(user, request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhoneNumber(), request.getProfilePictureUrl());

        if (request.getAddress() != null) {
            owner.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            owner.setCity(request.getCity());
        }
        if (request.getState() != null) {
            owner.setState(request.getState());
        }
        if (request.getDriversLicenseNumber() != null
                && !request.getDriversLicenseNumber().equals(owner.getDriversLicenseNumber())) {
            ownersRepository.findByDriversLicenseNumber(request.getDriversLicenseNumber())
                    .filter(existing -> !existing.getOwnerId().equals(owner.getOwnerId()))
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver's license number already in use");
                    });
            owner.setDriversLicenseNumber(request.getDriversLicenseNumber());
        }

        usersRepository.save(user);
        ownersRepository.save(owner);

        return mapOwner(owner);
    }

    public OfficerProfileResponse updateOfficer(UUID officerId, OfficerUpdateRequest request) {
        if (officerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Officer ID is required");
        }

        Officers officer = officersRepository.findById(officerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Officer not found"));

        Users user = officer.getUsers();
        updateUserFields(user, request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhoneNumber(), request.getProfilePictureUrl());

        if (request.getBadgeNumber() != null && !request.getBadgeNumber().equals(officer.getBadgeNumber())) {
            officersRepository.findByBadgeNumber(request.getBadgeNumber())
                    .filter(existing -> !existing.getOfficerId().equals(officer.getOfficerId()))
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Badge number already in use");
                    });
            officer.setBadgeNumber(request.getBadgeNumber());
        }
        if (request.getDepartment() != null) {
            officer.setDepartment(request.getDepartment());
        }
        if (request.getRank() != null) {
            officer.setRank(request.getRank());
        }
        if (request.getAssignmentArea() != null) {
            officer.setAssignmentArea(request.getAssignmentArea());
        }

        usersRepository.save(user);
        officersRepository.save(officer);

        return mapOfficer(officer);
    }

    private void updateUserFields(Users user, String firstName, String lastName, String email, String phoneNumber, String profilePictureUrl) {
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (email != null && !email.equals(user.getEmail())) {
            usersRepository.findByEmail(email)
                    .filter(existing -> !existing.getUserId().equals(user.getUserId()))
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
                    });
            user.setEmail(email);
        }
        if (phoneNumber != null && !phoneNumber.equals(user.getPhoneNumber())) {
            usersRepository.findByPhoneNumber(phoneNumber)
                    .filter(existing -> !existing.getUserId().equals(user.getUserId()))
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number already in use");
                    });
            user.setPhoneNumber(phoneNumber);
        }
        if (profilePictureUrl != null) {
            user.setProfilePictureUrl(profilePictureUrl);
        }
    }

    private OwnerLookupResponse mapOwner(Owners owner) {
        OwnerLookupResponse response = new OwnerLookupResponse();
        response.setUserId(owner.getOwner().getUserId());
        response.setFirstName(owner.getOwner().getFirstName());
        response.setLastName(owner.getOwner().getLastName());
        response.setEmail(owner.getOwner().getEmail());
        response.setPhoneNumber(owner.getOwner().getPhoneNumber());
        response.setProfilePictureUrl(owner.getOwner().getProfilePictureUrl());
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

    private OfficerProfileResponse mapOfficer(Officers officer) {
        OfficerProfileResponse response = new OfficerProfileResponse();
        response.setUserId(officer.getUsers().getUserId());
        response.setFirstName(officer.getUsers().getFirstName());
        response.setLastName(officer.getUsers().getLastName());
        response.setEmail(officer.getUsers().getEmail());
        response.setPhoneNumber(officer.getUsers().getPhoneNumber());
        response.setProfilePictureUrl(officer.getUsers().getProfilePictureUrl());
        response.setRole(officer.getUsers().getRole());
        response.setOfficerDetails(mapOfficerDetails(officer));
        return response;
    }

    private OfficerDetailsResponse mapOfficerDetails(Officers officer) {
        OfficerDetailsResponse details = new OfficerDetailsResponse();
        details.setBadgeNumber(officer.getBadgeNumber());
        details.setDepartment(officer.getDepartment());
        details.setRank(officer.getRank());
        details.setAssignmentArea(officer.getAssignmentArea());
        return details;
    }
}
