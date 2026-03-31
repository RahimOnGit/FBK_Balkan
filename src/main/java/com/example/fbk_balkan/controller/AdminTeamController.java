package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.team.TeamCreateDto;
import com.example.fbk_balkan.dto.team.TeamListItemDTO;
import com.example.fbk_balkan.entity.Role;
import com.example.fbk_balkan.entity.Team;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.repository.UserRepository;
import com.example.fbk_balkan.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/teams")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminTeamController {

    private final TeamService teamService;
    private final UserRepository userRepository;

    @GetMapping
    public String listTeams(Model model) {
        List<TeamListItemDTO> teams = teamService.findAllForAdminList();
        model.addAttribute("teams", teams);
        return "admin/teams/list";
    }

    // ────────────
    // (edit form)
    // ─────────────
    @GetMapping("/edit/{id}")
    public String editTeamForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Team team = teamService.getTeamById(id);
        if (team == null) {
            ra.addFlashAttribute("error", "Laget är inte närvarande.");
            return "redirect:/admin/teams";
        }

        // Att konvertera enheten till en DTO är lämpligt för modellen
        TeamCreateDto dto = new TeamCreateDto();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setAgeGroup(team.getAgeGroup());
        dto.setGender(team.getGender().name());
        dto.setTrainingLocation(team.getTrainingLocation());
        dto.setDescription(team.getDescription());
        dto.setActive(team.isActive());
        if (team.getCoach() != null) {
            dto.setCoachId(team.getCoach().getId());
        }
        dto.setAssistantCoachIds(
                team.getAssistantCoaches() != null
                        ? team.getAssistantCoaches().stream()
                        .map(User::getId)
                        .toList()
                        : List.of()
        );
        model.addAttribute("team", dto);
        model.addAttribute("coaches", userRepository.findByRole(Role.COACH));
        model.addAttribute("isEdit", true);
        model.addAttribute("assistantCoaches", userRepository.findByRole(Role.ASSISTANT_COACH));
        return "admin/teams/form";   // ← Samma modell som användes för att skapa teamet
    }


    // ──────────────────────────────────────────
    // Spara ändringar eller skapa ett nytt team
    // ─────────────────────────────────────────
    @PostMapping("/save")
    public String saveTeam(
            @Valid @ModelAttribute("team") TeamCreateDto dto,
            BindingResult result,
            RedirectAttributes ra,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("coaches", userRepository.findByRole(Role.COACH));
            model.addAttribute("assistantCoaches", userRepository.findByRole(Role.ASSISTANT_COACH));
            model.addAttribute("isEdit", dto.getId() != null);
            // validation for assistant
            model.addAttribute("error", "Huvudtränare kan inte vara assistenttränare i samma lag");
            return "admin/teams/form";
        }

        try {
            if (dto.getId() == null) {
                // Skapa en ny
                teamService.createTeam(dto);
                ra.addFlashAttribute("success", "Teamet etablerades framgångsrikt");
            } else {

                teamService.updateTeam(dto.getId(), dto);
                ra.addFlashAttribute("success", "Teamet modifierades framgångsrikt");
            }
            return "redirect:/admin/teams";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ett fel uppstod: " + e.getMessage());
            model.addAttribute("coaches", userRepository.findByRole(Role.COACH));
            model.addAttribute("assistantCoaches", userRepository.findByRole(Role.ASSISTANT_COACH)); // NEW
            model.addAttribute("isEdit", dto.getId() != null);
            return "admin/teams/form";
        }
    }

    // ─────────
    //Ta bort laget
    // ─────────
    @PostMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Long id, RedirectAttributes ra) {
        try {
            teamService.deleteTeam(id);
            ra.addFlashAttribute("success", "Teamet har raderats");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Teamet kan inte raderas: " + e.getMessage());
        }
        return "redirect:/admin/teams";
    }

}