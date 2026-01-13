package com.example.fbk_balkan.dto;
import java.util.List;


public record ChildResponse(
        Long childId,
        String fullName,
        Integer birthYear,
        String teamCategory,
        String notes,
        List<TeamDTO> teams
) {}
