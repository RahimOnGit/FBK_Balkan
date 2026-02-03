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

import java.util.Optional;

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
//validate and fetch coach
        Coach coach = coachRepository.findById(teamCreateDto.getCoachId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid coach ID"));

//    convert DTO to entity
        Team team = teamMapper.toEntity(teamCreateDto, coach);

//    save entity
        Team savedTeam = teamRepository.save(team);
//    convert entity to DTO and return
        return teamMapper.toDto(savedTeam);
    }

    }
