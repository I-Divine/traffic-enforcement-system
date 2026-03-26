package com.project.traffic_enforcement.dto;

import java.util.UUID;

import com.project.traffic_enforcement.models.Users;
import com.project.traffic_enforcement.models.enums.Roles;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private UUID userId;
    private String email;
    private Roles role;
    private String profilePictureUrl;

    public static AuthResponse from(Users user, String token) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserId(user.getUserId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setProfilePictureUrl(user.getProfilePictureUrl());
        return response;
    }
}
