package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.entity.TrialStatus;
import com.example.fbk_balkan.repository.CoachRepository;
import com.example.fbk_balkan.repository.PlayerRepository;
import com.example.fbk_balkan.repository.TeamRepository;
import com.example.fbk_balkan.repository.TrialRegistrationRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final TrialRegistrationRepository trialRegistrationRepository;
    private final CoachRepository coachRepository;

    public AdminDashboardController(
            TeamRepository teamRepository,
            PlayerRepository playerRepository,
            TrialRegistrationRepository trialRegistrationRepository,
            CoachRepository coachRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.trialRegistrationRepository = trialRegistrationRepository;
        this.coachRepository = coachRepository;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {

        long teamCount     = teamRepository.count();
        long playerCount   = playerRepository.count();


        long pendingTrials = trialRegistrationRepository.countByStatus(TrialStatus.PENDING);


        // long pendingTrials = trialRegistrationRepository.countByStatus(TrialStatus.PENDING);

        List<TrialRegistration> latestTrials = trialRegistrationRepository
                .findTop10ByOrderByCreatedAtDesc();

        model.addAttribute("teamCount",     teamCount);
        model.addAttribute("playerCount",   playerCount);
        model.addAttribute("pendingTrials", pendingTrials);
        model.addAttribute("latestTrials",  latestTrials);

        return "admin/dashboard";
    }
}