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
import java.util.Optional;
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




}
