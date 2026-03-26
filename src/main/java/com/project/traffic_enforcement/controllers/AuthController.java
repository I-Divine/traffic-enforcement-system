package com.project.traffic_enforcement.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.traffic_enforcement.dto.AuthResponse;
import com.project.traffic_enforcement.dto.ChangePasswordRequest;
import com.project.traffic_enforcement.dto.ChangePasswordResponse;
import com.project.traffic_enforcement.dto.LoginRequest;
import com.project.traffic_enforcement.dto.RegisterRequest;
import com.project.traffic_enforcement.services.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN','REGISTRATION_OFFICERS')")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(request.getOldPassword(), request.getNewPassword()));
    }
}
