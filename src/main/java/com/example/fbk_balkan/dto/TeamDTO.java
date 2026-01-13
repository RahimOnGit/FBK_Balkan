package com.example.fbk_balkan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
    private Long teamId;
    private String name;
    private String description;
    private List<ChildDTO> children;
    private CoachDTO coach;
}

