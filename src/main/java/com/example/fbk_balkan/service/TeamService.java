package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.team.*;
import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.entity.Team;
import com.example.fbk_balkan.repository.CoachRepository;
import com.example.fbk_balkan.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {
@Autowired
private TeamRepository teamRepository;

@Autowired
private TeamMapper teamMapper;
    @Autowired
    private CoachRepository coachRepository;
    @Autowired
    private PublicTeamMapper publicTeamMapper;


    @Transactional
public TeamDto createTeam(TeamCreateDto teamCreateDto) {
        if (teamRepository.existsByNameAndAgeGroup(
                teamCreateDto.getName(),
                teamCreateDto.getAgeGroup()
        )) {
            throw new IllegalArgumentException("Lag finns redan för vald åldersgrupp");
        }

//validate and fetch coach
        Coach coach = coachRepository.findById(teamCreateDto.getCoachId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid coach ID"));

//    convert DTO to entity
        Team team = teamMapper.toEntity(teamCreateDto, coach);
        team.setCreatedDate(LocalDateTime.now());
        team.setUpdatedDate(LocalDateTime.now());

//    save entity
        Team savedTeam = teamRepository.save(team);
        //  access to coach while transaction is open
        savedTeam.getCoach().getEmail();
//    convert entity to DTO and return
        return teamMapper.toDto(savedTeam);
    }
    /**
     * Fetch all teams for a specific coach
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsByCoachId(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new IllegalArgumentException("Coach not found"));

        List<Team> teams = teamRepository.findByCoachId(coachId);

        return teams.stream()
                .map(teamMapper::toDto)
                .collect(Collectors.toList());
    }
//
public List<TeamListItemDTO> findAllForAdminList() {
    return teamRepository.findAll().stream()
            .map(team -> {
                TeamListItemDTO dto = new TeamListItemDTO();
                dto.setId(team.getId());
                dto.setName(team.getName());
                dto.setAgeGroup(team.getAgeGroup());
                dto.setGender(team.getGender());
                dto.setActive(team.isActive());
                dto.setCoachName(
                        team.getCoach() != null
                                ? team.getCoach().getFirstName() + " " + team.getCoach().getLastName()
                                : "—"
                );
                return dto;
            })
            .toList();
}


public List<Team> getActiveTeams() {
    return teamRepository.findByActiveTrue();
    // only active teams
}
    public List<PublicTeamDto> getSortedPublicTeams() {
        return getActiveTeams().stream()
                .map(publicTeamMapper::toDto)
                .sorted(Comparator.comparingInt((PublicTeamDto team) -> {
            try {
                String yearStr = team.getAgeGroup()
                        .replaceAll(".*\\((\\d{4})\\)", "$1");
                return Integer.parseInt(yearStr);
            } catch (Exception e) {
                return 0;
            }
        }).reversed())

                .toList();

    }
    public Team getActiveTeamById(Long id) {
        return teamRepository.findByIdAndActiveTrue(id).orElse(null);
    }



    /**
     * Fetch all teams for a specific coach by email
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsByCoachEmail(String email) {
        Coach coach = coachRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Coach not found"));

        return getTeamsByCoachId(coach.getId());
    }

    public List<TeamListItemDTO> findLatestTeams(int limit) {
        return teamRepository.findAllByOrderByCreatedDateDesc()
                .stream()
                .limit(limit)
                .map(team -> {
                    TeamListItemDTO dto = new TeamListItemDTO();
                    dto.setId(team.getId());
                    dto.setName(team.getName());
                    dto.setAgeGroup(team.getAgeGroup());
//                    dto.setGender(team.getGender() != null ? team.getGender().name() : "—");
                    dto.setGender(team.getGender() != null ? team.getGender() : null);
                    dto.setActive(team.isActive());
                    dto.setCoachName(team.getCoach() != null
                            ? team.getCoach().getFirstName() + " " + team.getCoach().getLastName()
                            : "—");

                    dto.setGenderDisplay(switch (team.getGender()) {
                        case MALE -> "Pojkar";
                        case FEMALE -> "Flickor";
//                        case MIXED -> "Mixat";
                        default -> "—";
                    });
                    dto.setCreatedDate(team.getCreatedDate());

                    return dto;
                })
                .collect(Collectors.toList());
    }

}



