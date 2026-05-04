package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.match.GameDTO;
import com.example.fbk_balkan.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MatchController {
@Autowired
    MatchService matchService;

    @GetMapping("/match-details/{id}")
    public String matchDetails(@PathVariable Long id, Model model) {

     GameDTO match = matchService.getMatchByGameNumber(id);
        model.addAttribute("match", match);
        return "public-pages/match-details";
    }
}
