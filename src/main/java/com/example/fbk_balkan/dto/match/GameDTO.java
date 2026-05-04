package com.example.fbk_balkan.dto.match;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
@JsonIgnoreProperties(ignoreUnknown = true)

public record GameDTO(
        Long gameNumber,
        Long homeTeamId,
        Long awayTeamId,
        String homeTeamName,
        String awayTeamName,
        String homeTeamImageUrl,
        String awayTeamImageUrl,
        Integer goalsScoredHomeTeam,
        Integer goalsScoredAwayTeam,
        String competitionName,
        String seasonName,
        String venueName,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timeAsDateTime ,
        Boolean isFinished,
        String result,
        String competitionCategoryName,
        String assistant1Name,
        String ageCategoryName,
        String venuePitchTypeName ,
         RefereesDTO referees   // ← nested object, matches JSON key exactly
) {
        // helper methods so the rest of the code doesn't need to change
        public String refereeName() {
                return referees != null ? referees.name() : null;
        }

        public String assistant1Name() {
                return referees != null ? referees.assistant1Name() : null;
        }
}