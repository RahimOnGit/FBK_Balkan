package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.match.GameDTO;
import com.example.fbk_balkan.dto.team.PublicTeamDto;
import com.example.fbk_balkan.dto.team.PublicTeamMapper;
import com.example.fbk_balkan.entity.Match;
import com.example.fbk_balkan.entity.Team;
import com.example.fbk_balkan.service.MatchService;
import com.example.fbk_balkan.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Controller
@RequestMapping("/public-teams")
public class PublicTeamController {

private final TeamService teamService;
    private final PublicTeamMapper publicTeamMapper;
    private final MatchService matchService;

    public PublicTeamController(TeamService teamService, PublicTeamMapper publicTeamMapper, MatchService matchService) {
        this.teamService = teamService;
        this.publicTeamMapper = publicTeamMapper;
        this.matchService = matchService;
    }
    @GetMapping("/{id}")
    public String showTeamPage(@PathVariable Long id, Model model) {
        Team team = teamService.getActiveTeamById(id);

        if (team == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Team not found"
            );
        }
        // Convert entity → DTO (secure public view)
        PublicTeamDto dto = publicTeamMapper.toDto(team);

        model.addAttribute("team", dto);

//        add matches model (upcoming matches , latest results)
        Long svffId = team.getSvffTeamId();
        model.addAttribute("upcomingMatches", matchService.fetchUpcomingMatchesForTeam(svffId));
        model.addAttribute("recentResults",   matchService.fetchRecentResultsForTeam(svffId));
        System.out.println("svffId: " + svffId);
        System.out.println("upcoming  " +matchService.fetchUpcomingMatchesForTeam(svffId));
        System.out.println("recent  " +matchService.fetchRecentResultsForTeam(svffId));
        return "public-pages/public-team";
    }

}
