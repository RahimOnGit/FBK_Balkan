package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.team.TeamCreateDto;
import com.example.fbk_balkan.dto.team.TeamDto;
import com.example.fbk_balkan.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/team-register")
public class TeamController {
    @Autowired
    private TeamService teamService;


    @PostMapping
    public String registerTeam(
            Model model,
            @Valid @ModelAttribute("team") TeamCreateDto teamDTO,
            BindingResult bindingResult )
    {
        if (bindingResult.hasErrors()) {

            return "private-pages/team-register";
        }


      TeamDto teamDto =  teamService.createTeam(teamDTO);
        model.addAttribute("successMessage","Team registration successful!");
        model.addAttribute("team", teamDto);
        return "private-pages/team-register-success";

    }
}
