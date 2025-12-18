package com.example.fbk_balkan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type;
    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private Object dashboardData; // Will contain children/teams based on role
    
    public JwtResponse(String token, Long userId, String email, String fullName, String role, Object dashboardData) {
        this.token = token;
        this.type = "Bearer";
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.dashboardData = dashboardData;
    }
}

