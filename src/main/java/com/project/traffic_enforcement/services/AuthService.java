package com.project.traffic_enforcement.services;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.traffic_enforcement.dto.AuthResponse;
import com.project.traffic_enforcement.dto.LoginRequest;
import com.project.traffic_enforcement.dto.OfficerDetailsRequest;
import com.project.traffic_enforcement.dto.OwnerDetailsRequest;
import com.project.traffic_enforcement.dto.RegisterRequest;
import com.project.traffic_enforcement.models.Officers;
import com.project.traffic_enforcement.models.Owners;
import com.project.traffic_enforcement.models.Users;
import com.project.traffic_enforcement.models.enums.Roles;
import com.project.traffic_enforcement.repository.OfficersRepository;
import com.project.traffic_enforcement.repository.OwnersRepository;
import com.project.traffic_enforcement.repository.UsersRepository;
import com.project.traffic_enforcement.security.config.UserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final OfficersRepository officersRepository;
    private final OwnersRepository ownersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }
        if (usersRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number already in use");
        }

        Roles role = request.getRole() != null ? request.getRole() : Roles.OWNERS;
        if (role == Roles.OWNERS) {
            OwnerDetailsRequest ownerDetails = request.getOwnerDetails();
            if (ownerDetails == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner details are required");
            }
            if (ownerDetails.getDriversLicenseNumber() != null
                    && ownersRepository.findByDriversLicenseNumber(ownerDetails.getDriversLicenseNumber()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver's license number already in use");
            }
        }
        if (role == Roles.ROAD_OFFICERS || role == Roles.APPEAL_OFFICERS) {
            OfficerDetailsRequest officerDetails = request.getOfficerDetails();
            if (officerDetails == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Officer details are required");
            }
            if (officerDetails.getBadgeNumber() != null
                    && officersRepository.findByBadgeNumber(officerDetails.getBadgeNumber()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Badge number already in use");
            }
        }

        Users user = new Users();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setCreatedAt(new Date());

        Users saved = usersRepository.save(user);

        if (role == Roles.OWNERS) {
            OwnerDetailsRequest ownerDetails = request.getOwnerDetails();
            Owners owner = new Owners();
            owner.setOwner(saved);
            owner.setAddress(ownerDetails.getAddress());
            owner.setCity(ownerDetails.getCity());
            owner.setState(ownerDetails.getState());
            owner.setDriversLicenseNumber(ownerDetails.getDriversLicenseNumber());
            ownersRepository.save(owner);
        }

        if (role == Roles.ROAD_OFFICERS || role == Roles.APPEAL_OFFICERS) {
            OfficerDetailsRequest officerDetails = request.getOfficerDetails();
            Officers officer = new Officers();
            officer.setUsers(saved);
            officer.setBadgeNumber(officerDetails.getBadgeNumber());
            officer.setDepartment(officerDetails.getDepartment());
            officer.setRank(officerDetails.getRank());
            officer.setAssignmentArea(officerDetails.getAssignmentArea());
            officersRepository.save(officer);
        }

        String token = jwtService.generateToken(UserDetails.build(saved));
        return AuthResponse.from(saved, token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found -> " + request.getEmail()));

        user.setLastLogin(new Date());
        usersRepository.save(user);

        String token = jwtService.generateToken(UserDetails.build(user));
        return AuthResponse.from(user, token);
    }
}
