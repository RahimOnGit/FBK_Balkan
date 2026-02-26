package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.team.TeamCreateDto;
import com.example.fbk_balkan.dto.team.TeamDto;
import com.example.fbk_balkan.entity.Role;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.repository.UserRepository;
import com.example.fbk_balkan.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;

@Controller
@RequestMapping("/team-register")
public class TeamController {
    @Autowired
    private TeamService teamService;
    private final UserRepository userRepository;

    public TeamController(TeamService teamService, UserRepository userRepository) {
        this.teamService = teamService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("team", new TeamCreateDto());
        model.addAttribute("coaches", userRepository.findByRole(Role.COACH)
                .stream()
                .sorted(Comparator.comparing(User::getLastName)
                        .thenComparing(User::getFirstName))
                .toList()
        );


        return "private-pages/team-register";
    }



@PostMapping
public String registerTeam(
        Model model,
        @Valid @ModelAttribute("team") TeamCreateDto teamDTO,
        BindingResult bindingResult) {

    //  Handle validation errors
    if (bindingResult.hasErrors()) {
//        model.addAttribute("coaches", coachRepository.findAll());

        //Bring in only users whose role is COACH
        model.addAttribute("coaches", userRepository.findByRole(Role.COACH));
        return "private-pages/team-register"; // show form with errors
    }

    // Call service to create team
    TeamDto createdTeam;
    try {
        createdTeam = teamService.createTeam(teamDTO);
    } catch (IllegalArgumentException ex) {
        bindingResult.rejectValue("name", "exists", ex.getMessage()); // use "name"
        model.addAttribute("coaches", userRepository.findAll());
        return "private-pages/team-register";
    }


    //  Add success message and team to the model
    model.addAttribute("successMessage", "Team registration successful!");
    model.addAttribute("team", createdTeam);

    // Render the same page but with success info
    return "private-pages/team-register-success";
}


}
