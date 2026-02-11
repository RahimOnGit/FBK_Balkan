package com.example.fbk_balkan.dto;

import com.example.fbk_balkan.entity.Team;
import lombok.Data;

@Data
public class TeamListItemDTO {
    private Long id;
    private String name;
    private String ageGroup;
    private Team.Gender gender;
    private String coachName;
    private boolean active;
}