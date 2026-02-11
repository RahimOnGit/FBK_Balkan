package com.example.fbk_balkan.dto;

import com.example.fbk_balkan.entity.Team;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TeamCreateUpdateDTO {
    private Long id;

    @NotBlank
    @Size(max=100)
    private String name;

    @NotBlank @Size(max=20)
    private String ageGroup;

    @NotNull
    private Team.Gender gender;

    @Size(max=150)
    private String trainingLocation;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Long coachId;           // ← viktig!

    private boolean active = true;
}
