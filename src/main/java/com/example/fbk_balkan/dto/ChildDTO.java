package com.example.fbk_balkan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildDTO {
    private Long childId;
    private String fullName;
    private Integer birthYear;
    private String teamCategory;
    private String notes;
    private List<TeamDTO> teams;
}

