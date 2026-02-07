package com.example.fbk_balkan.dto;

import com.example.fbk_balkan.entity.Role;
import lombok.Data;

@Data
public class CreateCoachDto {

    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Role role;


}
