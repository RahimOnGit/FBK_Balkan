package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.team.TeamCreateDto;
import com.example.fbk_balkan.dto.team.TeamDto;
import com.example.fbk_balkan.dto.team.TeamMapper;
import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.entity.Team;
import com.example.fbk_balkan.repository.CoachRepository;
import com.example.fbk_balkan.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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


    @Transactional
    public TeamDto createTeam(TeamCreateDto teamCreateDto) {
        if (teamRepository.existsByNameAndAgeGroup(
                teamCreateDto.getName(),
                teamCreateDto.getAgeGroup()
        )) {
            throw new IllegalArgumentException("Lag finns redan för vald åldersgrupp");
        }

        // validate and fetch coach
        Coach coach = coachRepository.findById(teamCreateDto.getCoachId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid coach ID"));

        // convert DTO to entity
        Team team = teamMapper.toEntity(teamCreateDto, coach);
        team.setCreatedDate(LocalDateTime.now());
        team.setUpdatedDate(LocalDateTime.now());

        // save entity
        Team savedTeam = teamRepository.save(team);
        // access to coach while transaction is open
        savedTeam.getCoach().getEmail();
        // convert entity to DTO and return
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

    /**
     * Fetch all teams for a specific coach by email
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsByCoachEmail(String email) {
        Coach coach = coachRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Coach not found"));

        return getTeamsByCoachId(coach.getId());
    }
}