package com.example.fbk_balkan.dto;

import lombok.Data;

@Data
public class UserProfileViewDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String roleLabel;
}