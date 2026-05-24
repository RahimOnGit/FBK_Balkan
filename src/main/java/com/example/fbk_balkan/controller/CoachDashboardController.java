package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
import com.example.fbk_balkan.dto.match.GameDTO;
import com.example.fbk_balkan.dto.team.TeamDto;
import com.example.fbk_balkan.entity.Player;
import com.example.fbk_balkan.entity.Team;
import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.entity.TrialStatus;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.repository.PlayerRepository;
import com.example.fbk_balkan.repository.TrialRegistrationRepository;
import com.example.fbk_balkan.repository.UserRepository;
import com.example.fbk_balkan.service.MatchService;
import com.example.fbk_balkan.service.TeamService;
import com.example.fbk_balkan.service.TrialEmailService;
import com.example.fbk_balkan.service.TrialRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CoachDashboardController {

    private final UserRepository coachRepository;
    private final TeamService teamService;
    private final TrialRegistrationService trialService;
    private final TrialRegistrationRepository trialRegistrationRepository;
    private final PlayerRepository playerRepository;
    private final MatchService matchService;
    private final TrialEmailService trialEmailService;   // ← ny

    public CoachDashboardController(UserRepository coachRepository,
                                    TeamService teamService,
                                    TrialRegistrationService trialService,
                                    TrialRegistrationRepository trialRegistrationRepository,
                                    PlayerRepository playerRepository,
                                    MatchService matchService,
                                    TrialEmailService trialEmailService) {
        this.coachRepository = coachRepository;
        this.teamService = teamService;
        this.trialService = trialService;
        this.trialRegistrationRepository = trialRegistrationRepository;
        this.playerRepository = playerRepository;
        this.matchService = matchService;
        this.trialEmailService = trialEmailService;
    }

    // ── Dashboard

    @GetMapping("/coach/dashboard")
    @PreAuthorize("hasRole('COACH')")
    public String dashboard(Model model,
                            @AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam(value = "show", required = false) String show) {

        String coachEmail = userDetails.getUsername();
        User coach = coachRepository.findByEmail(coachEmail).orElse(null);

        if (coach != null) {
            model.addAttribute("coachName", coach.getFirstName() + " " + coach.getLastName());

            List<TeamDto> teams = teamService.getTeamsByCoachId(coach.getId());
            model.addAttribute("teams", teams);
            model.addAttribute("teamCount", teams.size());

            List<TrialRegistrationDTO> allRequests =
                    trialService.fetchTrialRegistrationByCoach(coach.getId());

            List<TrialRegistrationDTO> activeRequests = allRequests.stream()
                    .filter(t -> t.getStatus() == TrialStatus.PENDING)
                    .toList();
            List<TrialRegistrationDTO> historyRequests = allRequests.stream()
                    .filter(t -> t.getStatus() != TrialStatus.PENDING)
                    .toList();

            model.addAttribute("activeRequests", activeRequests);
            model.addAttribute("historyRequests", historyRequests);
            model.addAttribute("pendingCount", (long) activeRequests.size());
            model.addAttribute("requestSize", allRequests.size());
        } else {
            model.addAttribute("coachName", "Coach");
            model.addAttribute("teams", List.of());
            model.addAttribute("teamCount", 0);
            model.addAttribute("activeRequests", List.of());
            model.addAttribute("historyRequests", List.of());
            model.addAttribute("requestSize", 0);
            model.addAttribute("pendingCount", 0);
        }

        if (show != null) {
            model.addAttribute("show", show);
        }

        return "coach/dashboard";
    }

    // ── Lagdetaljsida

    @GetMapping("/coach/team/{id}")
    @PreAuthorize("hasRole('COACH')")
    public String teamDetail(@PathVariable Long id,
                             Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {

        String coachEmail = userDetails.getUsername();
        User coach = coachRepository.findByEmail(coachEmail).orElse(null);
        if (coach == null) return "redirect:/coach/dashboard";

        Team team = teamService.getTeamById(id);
        if (team == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lag hittades inte");

        boolean ownsTeam = team.getCoach() != null
                && team.getCoach().getId().equals(coach.getId());
        if (!ownsTeam)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Du har inte tillgång till detta lag");

        List<Player> players =
                playerRepository.findByTeamIdAndActiveTrueOrderByLastNameAsc(team.getId());
        List<GameDTO> upcomingMatches =
                matchService.fetchUpcomingMatchesForTeam(team.getSvffTeamId());
        List<GameDTO> recentResults =
                matchService.fetchRecentResultsForTeam(team.getSvffTeamId());

        model.addAttribute("coachName",
                coach.getFirstName() + " " + coach.getLastName());
        model.addAttribute("team", team);
        model.addAttribute("players", players);
        model.addAttribute("upcomingMatches", upcomingMatches);
        model.addAttribute("recentResults", recentResults);

        return "coach/team-detail";
    }

    // ── Godkänn provträning

    @PostMapping("/coach/trial/{id}/approve")
    @PreAuthorize("hasRole('COACH')")
    public String approveTrialRequest(@PathVariable Long id,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      RedirectAttributes ra) {

        TrialRegistration reg = findAndVerifyOwnership(id, userDetails, ra);
        if (reg == null) return "redirect:/coach/dashboard";

        // Spara status
        reg.setStatus(TrialStatus.APPROVED);
        trialRegistrationRepository.save(reg);

        // Skicka mejl direkt via SMTP – inget Outlook behövs
        boolean sent = trialEmailService.sendApprovalEmail(reg);

        if (sent) {
            ra.addFlashAttribute("successMessage",
                    "Provträning godkänd och bekräftelsemejl skickat till "
                            + reg.getRelativeEmail());
        } else {
            ra.addFlashAttribute("successMessage",
                    "Provträning godkänd. (Mejlet kunde inte skickas – kontrollera SMTP-inställningarna.)");
        }

        return "redirect:/coach/dashboard";
    }

    // ── Avvisa provträning

    @PostMapping("/coach/trial/{id}/reject")
    @PreAuthorize("hasRole('COACH')")
    public String rejectTrialRequest(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes ra) {

        TrialRegistration reg = findAndVerifyOwnership(id, userDetails, ra);
        if (reg == null) return "redirect:/coach/dashboard";

        // Spara status
        reg.setStatus(TrialStatus.REJECTED);
        trialRegistrationRepository.save(reg);

        // Skicka mejl direkt via SMTP
        boolean sent = trialEmailService.sendRejectionEmail(reg);

        if (sent) {
            ra.addFlashAttribute("successMessage",
                    "Provträning avvisad och meddelande skickat till "
                            + reg.getRelativeEmail());
        } else {
            ra.addFlashAttribute("successMessage",
                    "Provträning avvisad. (Mejlet kunde inte skickas – kontrollera SMTP-inställningarna.)");
        }

        return "redirect:/coach/dashboard";
    }

    // ── Hjälpmetod: hämta & verifiera ägarskap

    private TrialRegistration findAndVerifyOwnership(Long id,
                                                     UserDetails userDetails,
                                                     RedirectAttributes ra) {
        TrialRegistration reg = trialRegistrationRepository.findById(id)
                .orElse(null);
        if (reg == null) {
            ra.addFlashAttribute("errorMessage", "Provträning hittades inte");
            return null;
        }

        User coach = coachRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (coach == null
                || reg.getCoach() == null
                || !reg.getCoach().getId().equals(coach.getId())) {
            ra.addFlashAttribute("errorMessage",
                    "Du har inte behörighet att hantera denna förfrågan");
            return null;
        }

        return reg;
    }
}