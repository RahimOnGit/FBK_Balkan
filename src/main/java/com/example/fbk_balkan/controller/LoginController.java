package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.service.LoginAttemptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            switch (error) {
                case "locked" -> model.addAttribute("error",
                        "Kontot är tillfälligt låst pga för många försök. Försök igen om "
                                + LoginAttemptService.LOCK_DURATION_MINUTES + " minuter.");
                case "invalid" -> model.addAttribute("error", "Felaktig e-post eller lösenord.");
                default -> model.addAttribute("error", "Felaktig e-post eller lösenord.");
            }
        }
        if (logout != null) {
            model.addAttribute("info", "Du har loggats ut.");
        }
        return "login";
    }

    // Kept for backward compatibility with any old links pointing here
    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("error", "Felaktig e-post eller lösenord.");
        return "login";
    }
}
