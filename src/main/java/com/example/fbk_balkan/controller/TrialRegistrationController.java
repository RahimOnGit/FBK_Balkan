package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.TrialRegistrationRequest;
import com.example.fbk_balkan.service.TrialRegistrationService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class TrialRegistrationController {

    private final TrialRegistrationService registrationService;

    public TrialRegistrationController(TrialRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/trial-registration")
    public String showForm(Model model) {
        model.addAttribute("trialRegistration", new TrialRegistrationRequest());
        return "register";
    }

    @PostMapping("/trial-registration")
    public String submitForm(@Valid @ModelAttribute("trialRegistration") TrialRegistrationRequest request,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
        try {
            registrationService.register(request);
            return "redirect:/trial-registration/success";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            return "register";
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("email", "error.email", "En registrering med denna e-postadress finns redan.");
            return "register";
        }
    }

    @GetMapping("/trial-registration/success")
    public String showSuccess() {
        return "trial-registration-success";
    }

    @GetMapping("/admin/trial-registrations")
    public String adminList(Model model) {
        model.addAttribute("registrations", registrationService.findAll());
        return "admin-trial-registrations";
    }
}



