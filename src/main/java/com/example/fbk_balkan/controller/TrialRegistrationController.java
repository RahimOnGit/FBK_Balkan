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

    private static final LocalDate CURRENT_YEAR = LocalDate.now();
    private static final LocalDate MIN_BIRTH_DATE = CURRENT_YEAR.minusYears(18); // Exempel: Från 18 år sedan och tillbaka
    private static final LocalDate MAX_BIRTH_DATE = CURRENT_YEAR.minusYears(5);  // Exempel: Upp till 5 år

    @GetMapping
    public String showTrialRegistrationForm(Model model) {
        model.addAttribute("trialRegistrationDTO", new TrialRegistrationDTO());
        model.addAttribute("availableGenders", Gender.values());
        model.addAttribute("availableReferralSources", ReferralSource.values());

        model.addAttribute("minBirthDate", MIN_BIRTH_DATE);
        model.addAttribute("maxBirthDate", MAX_BIRTH_DATE);



        return "trial-registration";

    }

    @PostMapping
    public String createTrialRegistration(
            @Valid @ModelAttribute("trialRegistrationDTO") TrialRegistrationDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        System.out.println("===== POST /trial-registration Åtkomst =====");

        if (bindingResult.hasErrors()) {
            System.out.println("Verifieringsfel: " + bindingResult.getErrorCount());

            // ← Fyll på listor och datum om det finns ett fel
            model.addAttribute("availableGenders", Gender.values());
            model.addAttribute("availableReferralSources", ReferralSource.values());
            model.addAttribute("minBirthDate", MIN_BIRTH_DATE);
            model.addAttribute("maxBirthDate", MAX_BIRTH_DATE);

            // Viktigt: Skicka in den angivna informationen igen så att den inte raderas
            model.addAttribute("trialRegistrationDTO", dto);

            return "trial-registration";
        }

        try {
            trialRegistrationService.create(dto);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Registreringen lyckades! Vi kontaktar dig snart.");

//            return "redirect:/trial-registration-success";

            return "redirect:/trial-registration-success";
        } catch (Exception e) {
            System.err.println("Fel vid sparning: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("errorMessage", "Ett fel uppstod under inspelningen: " + e.getMessage());
            model.addAttribute("availableGenders", Gender.values());
            model.addAttribute("availableReferralSources", ReferralSource.values());
            model.addAttribute("minBirthDate", MIN_BIRTH_DATE);
            model.addAttribute("maxBirthDate", MAX_BIRTH_DATE);
            model.addAttribute("trialRegistrationDTO", dto);

            redirectAttributes.addFlashAttribute("errorMessage", "Något gick fel. Försök igen.");
//            return "trial-registration";
            return "redirect:/trial-registration";
        }


    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "trial-registration-success";
    }

}