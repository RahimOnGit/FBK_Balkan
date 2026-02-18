package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.team.TeamListItemDTO;
import com.example.fbk_balkan.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/teams")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminTeamController {

    private final TeamService teamService;

    @GetMapping
    public String listTeams(Model model) {
        List<TeamListItemDTO> teams = teamService.findAllForAdminList();
        model.addAttribute("teams", teams);
        return "admin/teams/list";
    }
}