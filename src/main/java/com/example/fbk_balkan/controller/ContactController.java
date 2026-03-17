package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.ContactFormDTO;
import com.example.fbk_balkan.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/kontakt")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping
    public String showContactPage(Model model) {
        if (!model.containsAttribute("contactForm")) {
            model.addAttribute("contactForm", new ContactFormDTO());
        }
        return "public-pages/kontakt";
    }

    @PostMapping
    public String submitContactForm(
            @Valid @ModelAttribute("contactForm") ContactFormDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "public-pages/kontakt";
        }

        contactService.sendContactEmail(dto);

        redirectAttributes.addFlashAttribute("successMessage",
                "Tack för ditt meddelande! Vi återkommer till dig så snart som möjligt.");
        return "redirect:/kontakt";
    }
}
