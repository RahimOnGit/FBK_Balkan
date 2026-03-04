package com.example.fbk_balkan.dto.team;

import lombok.Data;

@Data
public class PublicTeamDto {
    private Long id; // <-- add this
    private String name;
    private String ageGroup;
    private String gender;
    private String description;

    private String coachName;       // First + Last
    private String trainingLocation;
}

