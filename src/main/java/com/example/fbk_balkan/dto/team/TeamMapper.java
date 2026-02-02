package com.example.fbk_balkan.dto.team;

import com.example.fbk_balkan.dto.CoachDto;
import com.example.fbk_balkan.dto.team.TeamCreateDto;
import com.example.fbk_balkan.dto.team.TeamDto;
import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    // Convert Entity → DTO (for responses)
    public TeamDto toDto(Team team) {
        if (team == null) return null;

        TeamDto dto = new TeamDto();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setAgeGroup(team.getAgeGroup());
        dto.setGender(team.getGender() != null ? team.getGender().name() : null);
        dto.setTrainingLocation(team.getTrainingLocation());
        dto.setActive(team.isActive());
        dto.setDescription(team.getDescription());
        dto.setCreatedDate(team.getCreatedDate());
        dto.setUpdatedDate(team.getUpdatedDate());

        if (team.getCoach() != null) {
            CoachDto coachDto = new CoachDto();
            coachDto.setId(team.getCoach().getId());
            coachDto.setUsername(team.getCoach().getUsername());
            coachDto.setEmail(team.getCoach().getEmail());
            dto.setCoach(coachDto);
        }

        return dto;
    }

    // Convert CreateDTO → Entity (for creating/updating)
    public Team toEntity(TeamCreateDto createDto, Coach coach) {
        if (createDto == null) return null;

        Team team = new Team();
        team.setName(createDto.getName());
        team.setAgeGroup(createDto.getAgeGroup());

        // Convert String to Enum
        if (createDto.getGender() != null) {
            team.setGender(Team.Gender.valueOf(createDto.getGender().toUpperCase()));
        }

        team.setTrainingLocation(createDto.getTrainingLocation());
        team.setDescription(createDto.getDescription());
        team.setActive(createDto.isActive());
        team.setCoach(coach); // Set the actual Coach entity

        return team;
    }
}