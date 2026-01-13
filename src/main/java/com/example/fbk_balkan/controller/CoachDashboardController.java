package com.example.fbk_balkan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CoachDashboardController {

    @GetMapping("/coach/dashboard")
    @PreAuthorize("hasRole('COACH')")
    public String dashboard() {
        return "coach/dashboard";
    }
}
