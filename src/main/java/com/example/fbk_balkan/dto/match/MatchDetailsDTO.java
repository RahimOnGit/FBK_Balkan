package com.example.fbk_balkan.dto.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MatchDetailsDTO(

        Long gameNumber,
        String competitionCategoryName,
        String result,


        String gameHomeTeamFormation,
        String gameAwayTeamFormation,

        @JsonProperty("name")
        String refereeName,

        String assistant1Name,
         String ageCategoryName


) {}