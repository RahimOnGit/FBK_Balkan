package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
import com.example.fbk_balkan.dto.team.TeamDto;
import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.repository.CoachRepository;
import com.example.fbk_balkan.service.TeamService;
import com.example.fbk_balkan.service.TrialRegistrationService;
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
    private final TrialRegistrationService trialService;

    public CoachDashboardController(CoachRepository coachRepository,
                                    TeamService teamService,
                                    TrialRegistrationService trialService) {
        this.coachRepository = coachRepository;
        this.teamService = teamService;
        this.trialService = trialService;
    }

    @GetMapping("/coach/dashboard")
    @PreAuthorize("hasRole('COACH')")
    public String dashboard(Model model,
                            @AuthenticationPrincipal UserDetails userDetails) {

        String coachEmail = userDetails.getUsername();
        Coach coach = coachRepository.findByEmail(coachEmail).orElse(null);

        if (coach != null) {

            // --- Coach identity ---
            model.addAttribute("coachName", coach.getFirstName() + " " + coach.getLastName());

            // --- Teams ---
            List<TeamDto> teams = teamService.getTeamsByCoachId(coach.getId());
            model.addAttribute("teams", teams);
            model.addAttribute("teamCount", teams.size());

            // Trial registration requests assigned to this coach ---
            // These are populated automatically by TrialRegistrationService.create()
            // which matches the child's birth year + gender to the coach's team
            List<TrialRegistrationDTO> trialRequests = trialService.fetchTrialRegistrationByCoach(coach.getId());
            model.addAttribute("trialRequests", trialRequests);

            // Count only PENDING requests to show as a badge/counter
            long pendingCount = trialRequests.stream()
                    .filter(t -> t.getStatus() != null &&
                            t.getStatus().name().equals("PENDING"))
                    .count();
            model.addAttribute("requestSize", trialRequests.size());
            model.addAttribute("pendingCount", pendingCount);

        } else {
            model.addAttribute("coachName", "Coach");
            model.addAttribute("teams", List.of());
            model.addAttribute("teamCount", 0);
            model.addAttribute("trialRequests", List.of());
            model.addAttribute("requestSize", 0);
            model.addAttribute("pendingCount", 0);
        }

        return "coach/dashboard";
    }
}