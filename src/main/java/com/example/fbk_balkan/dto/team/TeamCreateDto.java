package com.example.fbk_balkan.dto.team;

import com.example.fbk_balkan.dto.CoachDto;
import com.example.fbk_balkan.entity.Team;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TeamCreateDto {
    @NotBlank @Size(max = 100)
    private String name;

    @NotBlank @Size(max = 20)
    private String ageGroup;

    @NotBlank
    private String gender;  // e.g., "MALE", "FEMALE" , "MIXED"

   @Size(max = 150)
    private String trainingLocation;

   @NotNull
    private Long coachId; // only id to create update --never full coach object

   @Size(max = 1000)
   private String description;

   private boolean active = true;


}

