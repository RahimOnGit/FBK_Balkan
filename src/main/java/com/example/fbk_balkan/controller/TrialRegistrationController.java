package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
import com.example.fbk_balkan.service.TrialRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

import java.time.LocalDate;

@Controller
@RequestMapping("/trial-registration")
public class TrialRegistrationController {

    @Autowired
    private TrialRegistrationService trialRegistrationService;


    @GetMapping
    public String showTrialRegistrationForm(Model model) {
        model.addAttribute("trialRegistrationDTO", new TrialRegistrationDTO());
        model.addAttribute("availableTrials", List.of(LocalDate.now().plusDays(3),
                                                    LocalDate.now().plusDays(5)));
        return "trial-registration";
    }
    @PostMapping
    public String createTrialRegistration(
            @Valid
            @ModelAttribute("trial")
            TrialRegistrationDTO trialRegistrationDTO ,
            BindingResult bindingResult , Model model) {

        if (bindingResult.hasErrors()) {
            return "trial-registration";
        }

        trialRegistrationService.create(trialRegistrationDTO);
        model.addAttribute("successMessage","Registration successful!");
        return "trial-registration-success";
      }

}
