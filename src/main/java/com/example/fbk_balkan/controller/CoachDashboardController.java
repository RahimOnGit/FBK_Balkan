package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.team.TeamDto;
import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.repository.CoachRepository;
import com.example.fbk_balkan.service.TeamService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CoachDashboardController {
    private final CoachRepository coachRepository;
    private final TeamService teamService;

    public CoachDashboardController(CoachRepository coachRepository, TeamService teamService) {
        this.coachRepository = coachRepository;
        this.teamService = teamService;
    }


    @GetMapping("/coach/dashboard")
    @PreAuthorize("hasRole('COACH')")
    public String dashboard(Model model,
                            @AuthenticationPrincipal UserDetails userDetails) {

        // Get coach details from userDetails and add to model
        String coachEmail = userDetails.getUsername();
        Coach coach = coachRepository.findByEmail(coachEmail).orElse(null);

        if (coach != null) {
            String coachName = coach.getFirstName() + " " + coach.getLastName();
            model.addAttribute("coachName", coachName);

            // Fetch teams for this coach
            List<TeamDto> teams = teamService.getTeamsByCoachId(coach.getId());
            model.addAttribute("teams", teams);
            model.addAttribute("teamCount", teams.size());
        } else {
            model.addAttribute("coachName", "coach");
            model.addAttribute("teams", List.of());
            model.addAttribute("teamCount", 0);
        }

        return "coach/dashboard";
    }
}