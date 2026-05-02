package com.example.fbk_balkan.mapper;

import com.example.fbk_balkan.dto.match.MatchDetailsDTO;
import com.example.fbk_balkan.entity.MatchDetails;
import org.springframework.stereotype.Component;


@Component
public class MatchDetailsMapper {

    public MatchDetails toEntity(MatchDetailsDTO dto) {

        MatchDetails entity = new MatchDetails();

        entity.setGameNumber(dto.gameNumber());
        entity.setCompetitionCategoryName(dto.competitionCategoryName());
        entity.setResult(dto.result());
        entity.setGameHomeTeamFormation(dto.gameHomeTeamFormation());
        entity.setGameAwayTeamFormation(dto.gameAwayTeamFormation());
        entity.setRefereeName(dto.refereeName());
        entity.setAssistant1Name(dto.assistant1Name());
        entity.setAgeCategoryName(dto.ageCategoryName());

        return entity;
    }

    public MatchDetailsDTO toDto(MatchDetails entity) {

        return new MatchDetailsDTO(
                entity.getGameNumber(),
                entity.getCompetitionCategoryName(),
                entity.getResult(),
                entity.getGameHomeTeamFormation(),
                entity.getGameAwayTeamFormation(),
                entity.getRefereeName(),
                entity.getAssistant1Name(),
                entity.getAgeCategoryName()
        );
    }
}