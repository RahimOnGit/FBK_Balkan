package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.config.LocalDateConverter;
import com.example.fbk_balkan.dto.CoachResponseDto;
import com.example.fbk_balkan.dto.CreateCoachDto;
import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
@Autowired
    private CoachService coachService;

@PostMapping("/register-coach")
    public CoachResponseDto createCoach(@RequestBody CreateCoachDto dto)
{
    Coach coach = coachService.createCoach(dto);

    CoachResponseDto res = new CoachResponseDto();
    res.setId(coach.getId());
    res.setFirstName(coach.getFirstName());
    res.setLastName(coach.getLastName());
    res.setEmail(coach.getEmail());
    res.setRole(coach.getRole());
    return res;
}

}
