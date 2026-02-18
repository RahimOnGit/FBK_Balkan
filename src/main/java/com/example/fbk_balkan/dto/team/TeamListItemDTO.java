package com.example.fbk_balkan.dto.team;

import com.example.fbk_balkan.entity.Team;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamListItemDTO {
    private Long id;
    private String name;
    private String ageGroup;
    private Team.Gender gender;
//    private String gender;
    private String genderDisplay;
    private String coachName;
    private boolean active;
    private LocalDateTime createdDate;
}