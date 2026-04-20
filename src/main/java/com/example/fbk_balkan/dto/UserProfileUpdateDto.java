package com.example.fbk_balkan.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserProfileUpdateDto {

    @Pattern(
            regexp = "^[0-9+\\-\\s]{6,20}$",
            message = "Ogiltigt telefonnummer"
    )
    private String phone;
}