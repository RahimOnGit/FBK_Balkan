//package com.example.fbk_balkan.controller;
//
//import com.example.fbk_balkan.dto.TrialRegistrationDTO;
//import com.example.fbk_balkan.dto.team.TeamDto;
//import com.example.fbk_balkan.entity.TrialRegistration;
//import com.example.fbk_balkan.entity.TrialStatus;
//import com.example.fbk_balkan.entity.User;
//import com.example.fbk_balkan.repository.TrialRegistrationRepository;
//import com.example.fbk_balkan.repository.UserRepository;
//import com.example.fbk_balkan.service.TeamService;
//import com.example.fbk_balkan.service.TrialRegistrationService;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//@Controller
//public class CoachDashboardController {
//    private final UserRepository coachRepository;
//    private final TeamService teamService;
//    private final TrialRegistrationService trialService;
//    private final TrialRegistrationRepository trialRegistrationRepository;
//
//    public CoachDashboardController(UserRepository coachRepository,
//                                    TeamService teamService,
//                                    TrialRegistrationService trialService,
//                                    TrialRegistrationRepository trialRegistrationRepository) {
//        this.coachRepository = coachRepository;
//        this.teamService = teamService;
//        this.trialService = trialService;
//        this.trialRegistrationRepository = trialRegistrationRepository;
//    }
//
//    @GetMapping("/coach/dashboard")
//    @PreAuthorize("hasRole('COACH')")
//    public String dashboard(Model model,
//                            @AuthenticationPrincipal UserDetails userDetails,
//                            @RequestParam(value = "show", required = false) String show) {
//
//        // Get coach details from userDetails and add to model
//        String coachEmail = userDetails.getUsername();
//        User coach = coachRepository.findByEmail(coachEmail).orElse(null);
//
//        if (coach != null) {
//            // --- Coach identity ---
//            model.addAttribute("coachName", coach.getFirstName() + " " + coach.getLastName());
//
//            // --- Teams ---
//            List<TeamDto> teams = teamService.getTeamsByCoachId(coach.getId());
//            model.addAttribute("teams", teams);
//            model.addAttribute("teamCount", teams.size());
//
//            // Trial registration requests assigned to this coach ---
//            List<TrialRegistrationDTO> trialRequests = trialService.fetchTrialRegistrationByCoach(coach.getId());
//            model.addAttribute("trialRequests", trialRequests);
//
//            // Count only PENDING requests to show as a badge/counter
//            long pendingCount = trialRequests.stream()
//                    .filter(t -> t.getStatus() != null &&
//                            t.getStatus().name().equals("PENDING"))
//                    .count();
//            model.addAttribute("requestSize", trialRequests.size());
//            model.addAttribute("pendingCount", pendingCount);
//
//        } else {
//            model.addAttribute("coachName", "Coach");
//            model.addAttribute("teams", List.of());
//            model.addAttribute("teamCount", 0);
//            model.addAttribute("trialRequests", List.of());
//            model.addAttribute("requestSize", 0);
//            model.addAttribute("pendingCount", 0);
//        }
//
//        if (show != null) {
//            model.addAttribute("show", show);
//        }
//
//        return "coach/dashboard";
//    }
//
//    @PostMapping("/coach/trial/{id}/approve")
//    @PreAuthorize("hasRole('COACH')")
//    public String approveTrialRequest(@PathVariable Long id,
//                                      @AuthenticationPrincipal UserDetails userDetails,
//                                      RedirectAttributes redirectAttributes) {
//
//        try {
//            TrialRegistration registration = trialRegistrationRepository.findById(id)
//                    .orElseThrow(() -> new IllegalArgumentException("Provträning hittades inte"));
//
//            // Verify this coach owns this request
//            String coachEmail = userDetails.getUsername();
//            User coach = coachRepository.findByEmail(coachEmail).orElse(null);
//
//            if (coach == null || !registration.getCoach().getId().equals(coach.getId())) {
//                redirectAttributes.addFlashAttribute("errorMessage", "Du har inte behörighet att hantera denna förfrågan");
//                return "redirect:/coach/dashboard";
//            }
//
//            // Update status to APPROVED
//            registration.setStatus(TrialStatus.APPROVED);
//            trialRegistrationRepository.save(registration);
//
//            // Build mailto link for approval email
//            String mailtoLink = buildApprovalMailto(registration);
//
//            redirectAttributes.addFlashAttribute("successMessage", "Status uppdaterad till godkänd");
//            redirectAttributes.addFlashAttribute("mailtoLink", mailtoLink);
//
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Något gick fel: " + e.getMessage());
//        }
//
//        return "redirect:/coach/dashboard";
//    }
//
//    @PostMapping("/coach/trial/{id}/reject")
//    @PreAuthorize("hasRole('COACH')")
//    public String rejectTrialRequest(@PathVariable Long id,
//                                     @AuthenticationPrincipal UserDetails userDetails,
//                                     RedirectAttributes redirectAttributes) {
//
//        try {
//            TrialRegistration registration = trialRegistrationRepository.findById(id)
//                    .orElseThrow(() -> new IllegalArgumentException("Provträning hittades inte"));
//
//            // Verify this coach owns this request
//            String coachEmail = userDetails.getUsername();
//            User coach = coachRepository.findByEmail(coachEmail).orElse(null);
//
//            if (coach == null || !registration.getCoach().getId().equals(coach.getId())) {
//                redirectAttributes.addFlashAttribute("errorMessage", "Du har inte behörighet att hantera denna förfrågan");
//                return "redirect:/coach/dashboard";
//            }
//
//            // Update status to REJECTED
//            registration.setStatus(TrialStatus.REJECTED);
//            trialRegistrationRepository.save(registration);
//
//            // Build mailto link for rejection email
//            String mailtoLink = buildRejectionMailto(registration);
//
//            redirectAttributes.addFlashAttribute("successMessage", "Status uppdaterad till avvisad");
//            redirectAttributes.addFlashAttribute("mailtoLink", mailtoLink);
//
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Något gick fel: " + e.getMessage());
//        }
//
//        return "redirect:/coach/dashboard";
//    }
//
//    // Helper method to build approval mailto link
//    private String buildApprovalMailto(TrialRegistration registration) {
//        String subject = "Provträning godkänd – FBK Balkan";
//
//        String body = "Hej " + registration.getRelativeName() + "!\n\n" +
//                "Vi är glada att meddela att " +
//                registration.getFirstName() + " " + registration.getLastName() +
//                " har blivit godkänd för provträning hos FBK Balkan.\n\n" +
//                "Vi ser fram emot att träffa er!\n\n" +
//                "Välkommen till laget!\n\n" +
//                "Med vänliga hälsningar,\n" +
//                "FBK Balkan";
//
//        return buildMailtoLink(registration.getRelativeEmail(), subject, body);
//    }
//
//    // Helper method to build rejection mailto link
//    private String buildRejectionMailto(TrialRegistration registration) {
//        String subject = "Angående din provträningsansökan – FBK Balkan";
//
//        String body = "Hej " + registration.getRelativeName() + "!\n\n" +
//                "Tack för att ni visade intresse för FBK Balkan.\n\n" +
//                "Vi har tyvärr inte möjlighet att erbjuda " +
//                registration.getFirstName() + " " + registration.getLastName() +
//                " en plats hos oss just nu.\n\n" +
//                "Vi önskar er lycka till och hoppas att ni hittar ett bra lag.\n\n" +
//                "Med vänliga hälsningar,\n" +
//                "FBK Balkan";
//
//        return buildMailtoLink(registration.getRelativeEmail(), subject, body);
//    }
//
//    // Helper method to construct mailto URL with proper encoding
//    private String buildMailtoLink(String email, String subject, String body) {
//        try {
//            // Using %20 for spaces instead of + (URLEncoder uses + for form data)
//            String encodedSubject = URLEncoder.encode(subject, StandardCharsets.UTF_8.toString())
//                    .replace("+", "%20");
//            String encodedBody = URLEncoder.encode(body, StandardCharsets.UTF_8.toString())
//                    .replace("+", "%20");
//
//            return "mailto:" + email +
//                    "?subject=" + encodedSubject +
//                    "&body=" + encodedBody;
//        } catch (UnsupportedEncodingException e) {
//            // Fallback without encoding
//            return "mailto:" + email;
//        }
//    }
//}

package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
import com.example.fbk_balkan.dto.team.TeamDto;
import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.entity.TrialStatus;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.repository.TrialRegistrationRepository;
import com.example.fbk_balkan.repository.UserRepository;
import com.example.fbk_balkan.service.TeamService;
import com.example.fbk_balkan.service.TrialRegistrationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class CoachDashboardController {
    private final UserRepository coachRepository;
    private final TeamService teamService;
    private final TrialRegistrationService trialService;
    private final TrialRegistrationRepository trialRegistrationRepository;

    public CoachDashboardController(UserRepository coachRepository,
                                    TeamService teamService,
                                    TrialRegistrationService trialService,
                                    TrialRegistrationRepository trialRegistrationRepository) {
        this.coachRepository = coachRepository;
        this.teamService = teamService;
        this.trialService = trialService;
        this.trialRegistrationRepository = trialRegistrationRepository;
    }

    @GetMapping("/coach/dashboard")
    @PreAuthorize("hasRole('COACH')")
    public String dashboard(Model model,
                            @AuthenticationPrincipal UserDetails userDetails) {

        // Get coach details from userDetails and add to model
        String coachEmail = userDetails.getUsername();
        User coach = coachRepository.findByEmail(coachEmail).orElse(null);

        if (coach != null) {
            // --- Coach identity ---
            model.addAttribute("coachName", coach.getFirstName() + " " + coach.getLastName());

            // --- Teams ---
            List<TeamDto> teams = teamService.getTeamsByCoachId(coach.getId());
            model.addAttribute("teams", teams);
            model.addAttribute("teamCount", teams.size());

            // Trial registration requests assigned to this coach ---
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

    @PostMapping("/coach/trial/{id}/approve")
    @PreAuthorize("hasRole('COACH')")
    public String approveTrialRequest(@PathVariable Long id,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {

        try {
            TrialRegistration registration = trialRegistrationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Provträning hittades inte"));

            // Verify this coach owns this request
            String coachEmail = userDetails.getUsername();
            User coach = coachRepository.findByEmail(coachEmail).orElse(null);

            if (coach == null || !registration.getCoach().getId().equals(coach.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Du har inte behörighet att hantera denna förfrågan");
                return "redirect:/coach/dashboard";
            }

            // Update status to APPROVED
            registration.setStatus(TrialStatus.APPROVED);
            trialRegistrationRepository.save(registration);

            // Build mailto link for approval email
            String mailtoLink = buildApprovalMailto(registration);

            redirectAttributes.addFlashAttribute("successMessage", "Status uppdaterad till godkänd");
            redirectAttributes.addFlashAttribute("mailtoLink", mailtoLink);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Något gick fel: " + e.getMessage());
        }

        return "redirect:/coach/dashboard";
    }

    @PostMapping("/coach/trial/{id}/reject")
    @PreAuthorize("hasRole('COACH')")
    public String rejectTrialRequest(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {

        try {
            TrialRegistration registration = trialRegistrationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Provträning hittades inte"));

            // Verify this coach owns this request
            String coachEmail = userDetails.getUsername();
            User coach = coachRepository.findByEmail(coachEmail).orElse(null);

            if (coach == null || !registration.getCoach().getId().equals(coach.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Du har inte behörighet att hantera denna förfrågan");
                return "redirect:/coach/dashboard";
            }

            // Update status to REJECTED
            registration.setStatus(TrialStatus.REJECTED);
            trialRegistrationRepository.save(registration);

            // Build mailto link for rejection email
            String mailtoLink = buildRejectionMailto(registration);

            redirectAttributes.addFlashAttribute("successMessage", "Status uppdaterad till avvisad");
            redirectAttributes.addFlashAttribute("mailtoLink", mailtoLink);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Något gick fel: " + e.getMessage());
        }

        return "redirect:/coach/dashboard";
    }

    // Helper method to build approval mailto link
    private String buildApprovalMailto(TrialRegistration registration) {
        String subject = "Provträning godkänd – FBK Balkan";

        String body = "Hej " + registration.getRelativeName() + "!\n\n" +
                "Vi är glada att meddela att " +
                registration.getFirstName() + " " + registration.getLastName() +
                " har blivit godkänd för provträning hos FBK Balkan.\n\n" +
                "Vi ser fram emot att träffa er!\n\n" +
                "Välkommen till laget!\n\n" +
                "Med vänliga hälsningar,\n" +
                "FBK Balkan";

        return buildMailtoLink(registration.getRelativeEmail(), subject, body);
    }

    // Helper method to build rejection mailto link
    private String buildRejectionMailto(TrialRegistration registration) {
        String subject = "Angående din provträningsansökan – FBK Balkan";

        String body = "Hej " + registration.getRelativeName() + "!\n\n" +
                "Tack för att ni visade intresse för FBK Balkan.\n\n" +
                "Vi har tyvärr inte möjlighet att erbjuda " +
                registration.getFirstName() + " " + registration.getLastName() +
                " en plats hos oss just nu.\n\n" +
                "Vi önskar er lycka till och hoppas att ni hittar ett bra lag.\n\n" +
                "Med vänliga hälsningar,\n" +
                "FBK Balkan";

        return buildMailtoLink(registration.getRelativeEmail(), subject, body);
    }

    // Helper method to construct mailto URL with proper encoding
    private String buildMailtoLink(String email, String subject, String body) {
        try {
            // Using %20 for spaces instead of + (URLEncoder uses + for form data)
            String encodedSubject = URLEncoder.encode(subject, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
            String encodedBody = URLEncoder.encode(body, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");

            return "mailto:" + email +
                    "?subject=" + encodedSubject +
                    "&body=" + encodedBody;
        } catch (UnsupportedEncodingException e) {
            // Fallback without encoding
            return "mailto:" + email;
        }
    }
}