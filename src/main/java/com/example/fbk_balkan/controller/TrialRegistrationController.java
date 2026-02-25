package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
import com.example.fbk_balkan.enums.Gender;
import com.example.fbk_balkan.enums.ReferralSource;
import com.example.fbk_balkan.service.TrialRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;
import java.time.LocalDate;

@Controller
public class TrialRegistrationController {

    @Autowired
    private TrialRegistrationService trialRegistrationService;

    private void addCommonModelAttributes(Model model) {
        model.addAttribute("availableGenders", Gender.values());
        model.addAttribute("availableReferralSources", ReferralSource.values());

        model.addAttribute("availableTrials", List.of(
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5)
        ));

        int currentYear = LocalDate.now().getYear();
        int yearsBack = 6;

        LocalDate minBirthDate = LocalDate.of(currentYear - yearsBack + 1, 1, 1);
        LocalDate maxBirthDate = LocalDate.of(currentYear, 12, 31);

        model.addAttribute("minBirthDate", minBirthDate);
        model.addAttribute("maxBirthDate", maxBirthDate);
    }

    @GetMapping("/trial-registration")
    public String showTrialRegistrationForm(Model model) {
        model.addAttribute("trialRegistrationDTO", new TrialRegistrationDTO());
        addCommonModelAttributes(model);
        return "trial-registration";
    }

    @PostMapping("/trial-registration")
    public String createTrialRegistration(
            @Valid
            @ModelAttribute("trialRegistrationDTO") TrialRegistrationDTO trialRegistrationDTO,
            BindingResult bindingResult,
            Model model) {

        if (trialRegistrationDTO.getReferralSource() != null &&
                trialRegistrationDTO.getReferralSource() != ReferralSource.OTHER) {
            trialRegistrationDTO.setReferralOther(null);
        }

        if (trialRegistrationDTO.getReferralOther() != null) {
            trialRegistrationDTO.setReferralOther(trialRegistrationDTO.getReferralOther().trim());
        }

        if (bindingResult.hasErrors()) {
            addCommonModelAttributes(model);
            return "trial-registration";
        }

        trialRegistrationService.create(trialRegistrationDTO);
        return "redirect:/trial-registration-success";
    }

    @GetMapping("/trial-registration-success")
    public String showSuccessPage() {
        return "trial-registration-success";
    }
}