package com.example.fbk_balkan.dto;

import com.example.fbk_balkan.entity.Role;
import lombok.Data;

@Data
public class UserListItemDTO {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private int teamCount;
    private boolean enabled;
    private String phone;
}
