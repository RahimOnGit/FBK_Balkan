package com.example.fbk_balkan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoachDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
}

