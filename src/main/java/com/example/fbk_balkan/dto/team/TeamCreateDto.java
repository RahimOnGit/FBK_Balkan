package com.example.fbk_balkan.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateDto {

    private Long id;

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
    private List<Long> assistantCoachIds = new ArrayList<>();
    public List<Long> getAssistantCoachIds() { return assistantCoachIds; }
    public void setAssistantCoachIds(List<Long> assistantCoachIds) { this.assistantCoachIds = assistantCoachIds; }

}

