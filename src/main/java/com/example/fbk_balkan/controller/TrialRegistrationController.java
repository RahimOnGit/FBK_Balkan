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
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/trial-registration")
public class TrialRegistrationController {

    @Autowired
    private TrialRegistrationService trialRegistrationService;

    @GetMapping
    public String showTrialRegistrationForm(Model model) {
        model.addAttribute("trialRegistrationDTO", new TrialRegistrationDTO());
        model.addAttribute("availableGenders", Gender.values());
        model.addAttribute("availableReferralSources", ReferralSource.values());

        // Example of available trials
        model.addAttribute("availableTrials", List.of(
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5)
        ));

        // Birth year range configuration
        int currentYear = LocalDate.now().getYear();  //2007 -> 2021
        int yearsBack = 6; // <-- change this to any number of years you want

        // Minimum and maximum birth dates
        LocalDate minBirthDate = LocalDate.of(currentYear - yearsBack + 1, 1, 1); // Jan 1 of earliest year
        LocalDate maxBirthDate = LocalDate.of(currentYear, 12, 31);               // Dec 31 current year

        // Add to model
        model.addAttribute("minBirthDate", minBirthDate);
        model.addAttribute("maxBirthDate", maxBirthDate);

        return "trial-registration";

    }

@PostMapping
public String createTrialRegistration(
        @Valid
        @ModelAttribute("trialRegistrationDTO") TrialRegistrationDTO trialRegistrationDTO,
        BindingResult bindingResult,
        Model model) {

    // NEW: Handle referralOther dynamically
    // If referralSource is NOT OTHER, clear referralOther
    // This ensures database does not store irrelevant text
    // =========================
    if (trialRegistrationDTO.getReferralSource() != null &&
            trialRegistrationDTO.getReferralSource() != ReferralSource.OTHER) {
        trialRegistrationDTO.setReferralOther(null);
    }

    // =========================
    // NEW: Trim referralOther input if present
    // Optional cleanup to remove accidental whitespace
    // =========================
    if (trialRegistrationDTO.getReferralOther() != null) {
        trialRegistrationDTO.setReferralOther(trialRegistrationDTO.getReferralOther().trim());
    }

    if (bindingResult.hasErrors()) {
        // NEW: Re-add enum values to model for dropdowns
        // This ensures the dropdowns keep all options after form reload
        model.addAttribute("availableGenders", Gender.values());
        model.addAttribute("availableReferralSources", ReferralSource.values());
        return "trial-registration"; // redisplay form with errors
    }


    trialRegistrationService.create(trialRegistrationDTO);

    // =========================
    // SUCCESS MESSAGE
    // =========================
    model.addAttribute("successMessage", "Registration successful!");
    return "trial-registration-success"; // redirect to success page
}


}
