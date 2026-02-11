package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.team.TeamDto;
import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.repository.CoachRepository;
import com.example.fbk_balkan.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamRestController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private CoachRepository coachRepository;

    /**
     * Get all teams for the authenticated coach
     */
    @GetMapping("/my-teams")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<List<TeamDto>> getMyTeams(@AuthenticationPrincipal UserDetails userDetails) {
        String coachEmail = userDetails.getUsername();
        List<TeamDto> teams = teamService.getTeamsByCoachEmail(coachEmail);
        return ResponseEntity.ok(teams);
    }

    /**
     * Get all teams for a specific coach by ID (admin only)
     */
    @GetMapping("/coach/{coachId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TeamDto>> getTeamsByCoach(@PathVariable Long coachId) {
        List<TeamDto> teams = teamService.getTeamsByCoachId(coachId);
        return ResponseEntity.ok(teams);
    }
}