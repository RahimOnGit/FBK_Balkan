package com.example.fbk_balkan.dto.team;

import com.example.fbk_balkan.dto.CoachDto;
import com.example.fbk_balkan.entity.Team;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class TeamDto {
    private Long id;
    private String name;
    private String ageGroup;
    private String gender; // Use String instead of enum for simpler JSON/API contract
    private String trainingLocation;
    private CoachDto coach; // Embedded coach info
    private boolean active;
    private String description;

    // Audit fields — useful for admin/debugging
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;



}

