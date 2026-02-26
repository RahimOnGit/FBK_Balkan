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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/trial-registration")
public class TrialRegistrationController {

    @Autowired
    private TrialRegistrationService trialRegistrationService;

    @ModelAttribute
    public void addBirthDateRange(Model model) {
        int currentYear = LocalDate.now().getYear();

        int minAge = 5;
        int maxAge = 19;

        LocalDate minBirthDate = LocalDate.of(currentYear - maxAge, 1, 1);
        LocalDate maxBirthDate = LocalDate.of(currentYear - minAge, 12, 31);

        model.addAttribute("minBirthDate", minBirthDate);
        model.addAttribute("maxBirthDate", maxBirthDate);
    }

    @GetMapping
    public String showTrialRegistrationForm(Model model) {
        model.addAttribute("trialRegistrationDTO", new TrialRegistrationDTO());
        model.addAttribute("availableGenders", Gender.values());
        model.addAttribute("availableReferralSources", ReferralSource.values());

        return "trial-registration";

    }

    @PostMapping
    public String createTrialRegistration(
            @Valid
            @ModelAttribute("trialRegistrationDTO") TrialRegistrationDTO trialRegistrationDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

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
        // Call service with try/catch to handle duplicates/business errors
        try {
            trialRegistrationService.create(trialRegistrationDTO);
        } catch (IllegalStateException ex) {
            model.addAttribute("availableGenders", Gender.values());
            model.addAttribute("availableReferralSources", ReferralSource.values());
            model.addAttribute("duplicateError", ex.getMessage());
            return "trial-registration"; // redisplay form with duplicate message
        }
        // flash message
        //  success redirect
        redirectAttributes.addFlashAttribute("successMessage",
                "Tack för att du registrerade ditt barn för provträning hos FBK Balkan. Vi återkommer inom kort.");

        return "redirect:/trial-registration/success";


    }
    @GetMapping("/success")
    public String showSuccessPage(Model model) {

        if (!model.containsAttribute("successMessage")) {
            return "redirect:/trial-registration";
        }

        return "trial-registration-success";
    }
}