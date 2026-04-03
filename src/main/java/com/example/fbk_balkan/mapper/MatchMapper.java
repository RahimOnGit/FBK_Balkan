package com.example.fbk_balkan.mapper;

import com.example.fbk_balkan.dto.match.GameDTO;
import com.example.fbk_balkan.entity.Match;
import org.springframework.stereotype.Component;

@Component
// MatchMapper.java
public class MatchMapper {

    public Match toEntity(GameDTO dto) {
        Match match = new Match();
        match.setGameNumber(dto.gameNumber());
        match.setHomeTeamName(dto.homeTeamName());
        match.setAwayTeamName(dto.awayTeamName());
        match.setHomeTeamImageUrl(dto.homeTeamImageUrl());
        match.setAwayTeamImageUrl(dto.awayTeamImageUrl());
        match.setGoalsScoredHomeTeam(dto.goalsScoredHomeTeam());
        match.setGoalsScoredAwayTeam(dto.goalsScoredAwayTeam());
        match.setCompetitionName(dto.competitionName());
        match.setSeasonName(dto.seasonName());
        match.setTimeAsDateTime(dto.timeAsDateTime());
        return match;
    }
}