package com.example.fbk_balkan.dto;

import com.example.fbk_balkan.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CoachListItemDTO {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private int teamCount;
    private boolean enabled;
}
