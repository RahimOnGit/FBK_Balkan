package com.example.fbk_balkan.dto.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RefereesDTO(
        String name,           // maps to referees.name  → refereeName
        String assistant1Name  // maps to referees.assistant1Name
) {}