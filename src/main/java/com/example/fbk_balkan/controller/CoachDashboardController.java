package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.repository.CoachRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CoachDashboardController {
    private final CoachRepository coachRepository;
    public CoachDashboardController(CoachRepository coachRepository) {
        this.coachRepository = coachRepository;
    }


    @GetMapping("/coach/dashboard")
    @PreAuthorize("hasRole('COACH')")
    public String dashboard(Model model ,
                            @AuthenticationPrincipal UserDetails userDetails

    )
    {

// get coach details from userDetails and add to model
        String coachEmail = userDetails.getUsername();
//        OR
        Coach coach = coachRepository.findByEmail(coachEmail).orElse(null);
        String coachName = coach != null ? coach.getFirstName() + " " + coach.getLastName() : "coach";

        model.addAttribute("coachName" , coachName);
        return "coach/dashboard";
    }
}
