package com.example.fbk_balkan.mapper;

import com.example.fbk_balkan.dto.match.GameDTO;
import com.example.fbk_balkan.dto.match.RefereesDTO;
import com.example.fbk_balkan.entity.Match;
import org.springframework.stereotype.Component;

@Component
// MatchMapper.java
public class MatchMapper {

    public Match toEntity(GameDTO dto) {
        Match match = new Match();
        match.setGameNumber(dto.gameNumber());
        match.setHomeTeamSvffId(dto.homeTeamId());
        match.setAwayTeamSvffId(dto.awayTeamId());
        match.setHomeTeamName(dto.homeTeamName());
        match.setAwayTeamName(dto.awayTeamName());
        match.setHomeTeamImageUrl(dto.homeTeamImageUrl());
        match.setAwayTeamImageUrl(dto.awayTeamImageUrl());
        match.setGoalsScoredHomeTeam(dto.goalsScoredHomeTeam());
        match.setGoalsScoredAwayTeam(dto.goalsScoredAwayTeam());
        match.setCompetitionName(dto.competitionName());
        match.setSeasonName(dto.seasonName());
        match.setTimeAsDateTime(dto.timeAsDateTime());
        match.setVenueName(dto.venueName());

        match.setIsFinished(dto.isFinished());
        match.setResult(dto.result());
        match.setCompetitionCategoryName(dto.competitionCategoryName());
        match.setRefereeName(dto.refereeName());
        match.setAssistant1Name(dto.assistant1Name());
        match.setAgeCategoryName(dto.ageCategoryName());
        match.setVenuePitchTypeName(dto.venuePitchTypeName());
        return match;
    }
    public GameDTO toDto(Match entity) {
        RefereesDTO referees = new RefereesDTO(entity.getRefereeName(), entity.getAssistant1Name());
        return new GameDTO(
                entity.getGameNumber(),
                entity.getHomeTeamSvffId(),
                entity.getAwayTeamSvffId(),
                entity.getHomeTeamName(),
                entity.getAwayTeamName(),
                entity.getHomeTeamImageUrl(),
                entity.getAwayTeamImageUrl(),
                entity.getGoalsScoredHomeTeam(),
                entity.getGoalsScoredAwayTeam(),
                entity.getCompetitionName(),
                entity.getSeasonName(),
                entity.getVenueName(),
                entity.getTimeAsDateTime(),
                entity.getIsFinished(),
                entity.getResult(),
                entity.getCompetitionCategoryName(),
                entity.getAssistant1Name(),
                entity.getAgeCategoryName(),
                entity.getVenuePitchTypeName(),
                referees


        );
    }

}