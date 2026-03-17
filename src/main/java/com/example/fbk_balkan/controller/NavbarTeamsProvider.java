package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.team.PublicTeamDto;
import com.example.fbk_balkan.dto.team.PublicTeamMapper;
import com.example.fbk_balkan.service.TeamService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class NavbarTeamsProvider {

    private final TeamService teamService;
    private final PublicTeamMapper mapper;

    public NavbarTeamsProvider(TeamService teamService, PublicTeamMapper mapper) {
        this.teamService = teamService;
        this.mapper = mapper;
    }

    @ModelAttribute("navbarTeams")
    public List<PublicTeamDto> populateNavbarTeams() {
        try {
            return teamService.getSortedPublicTeams();
        } catch (Exception e) {
            return List.of();
        }
    }
}