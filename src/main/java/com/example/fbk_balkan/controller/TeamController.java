package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.CoachDto;
import com.example.fbk_balkan.dto.team.TeamCreateDto;
import com.example.fbk_balkan.dto.team.TeamDto;
import com.example.fbk_balkan.repository.CoachRepository;
import com.example.fbk_balkan.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/team-register")
public class TeamController {
    @Autowired
    private TeamService teamService;
    private final CoachRepository coachRepository;

    public TeamController(TeamService teamService, CoachRepository coachRepository) {
        this.teamService = teamService;
        this.coachRepository = coachRepository;
    }
    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("team", new TeamCreateDto());
        model.addAttribute("coaches", coachRepository.findAll());
        return "private-pages/team-register";
    }

//
//    @PostMapping
//    public String registerTeam(
//            Model model,
//            @Valid @ModelAttribute("team") TeamCreateDto teamDTO,
//            RedirectAttributes redirectAttributes,
//            BindingResult bindingResult  ) {
//
//        if (bindingResult.hasErrors()) {
//
//
//           // Flash attributes here
//            return "redirect:/team-register/success";
//
//        }
//
//
//        try {
//            TeamDto savedTeam = teamService.createTeam(teamDTO);
//
//
//            // Only send team name to success page
//            redirectAttributes.addFlashAttribute("teamName", savedTeam.getName());
//
//            return "private-pages/team-register-success";
//
//        } catch (IllegalArgumentException ex) {
//
//            bindingResult.reject(null, ex.getMessage());
//            model.addAttribute("coaches", coachRepository.findAll());
//
//            return "private-pages/team-register";
//        }
//
//
//    }
//    // extra end point to success page implemnting to stop rsubmiting of form
//    @GetMapping("/success")
//    public String teamRegisterSuccess(@ModelAttribute("teamName") String teamName) {
//        // If someone tries to access directly, redirect to form
//        if (teamName == null || teamName.isEmpty()) {
//            return "redirect:/team-register";
//        }
//        return "private-pages/team-register-success";
//    }
@PostMapping
public String registerTeam(
        Model model,
        @Valid @ModelAttribute("team") TeamCreateDto teamDTO,
        BindingResult bindingResult) {

    //  Handle validation errors
    if (bindingResult.hasErrors()) {
        model.addAttribute("coaches", coachRepository.findAll());
        return "private-pages/team-register"; // show form with errors
    }

    // Call service to create team
    TeamDto createdTeam;
    try {
        createdTeam = teamService.createTeam(teamDTO);
    } catch (IllegalArgumentException ex) {
        bindingResult.rejectValue("name", "exists", ex.getMessage()); // use "name"
        model.addAttribute("coaches", coachRepository.findAll());
        return "private-pages/team-register";
    }


    //  Add success message and team to the model
    model.addAttribute("successMessage", "Team registration successful!");
    model.addAttribute("team", createdTeam);

    // Render the same page but with success info
    return "private-pages/team-register-success";
}


}
