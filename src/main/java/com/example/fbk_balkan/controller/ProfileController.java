package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.UserProfileUpdateDto;
import com.example.fbk_balkan.dto.UserProfileViewDto;
import com.example.fbk_balkan.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // GET PROFILE
    // =========================
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String profile(Model model,
                          @AuthenticationPrincipal UserDetails userDetails) {

        UserProfileViewDto dto =
                userService.getCurrentUserProfile(userDetails.getUsername());

        model.addAttribute("user", dto);
        // pre-fill phone form
        UserProfileUpdateDto phoneForm = new UserProfileUpdateDto();
        phoneForm.setPhone(dto.getPhone());

        model.addAttribute("phoneForm", phoneForm);
        return "private-pages/profile";
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile/phone")
    public String updatePhone(
            @ModelAttribute("phoneForm") @Valid UserProfileUpdateDto dto,
            BindingResult result,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {

            UserProfileViewDto user =
                    userService.getCurrentUserProfile(userDetails.getUsername());

            model.addAttribute("user", user);

            //  keep value in form
            UserProfileUpdateDto phoneForm = new UserProfileUpdateDto();
            phoneForm.setPhone(dto.getPhone());

            model.addAttribute("phoneForm", phoneForm);

            return "private-pages/profile";
        }

        userService.updatePhone(userDetails.getUsername(), dto.getPhone());

        ra.addFlashAttribute("success", "Telefon uppdaterad!");
        return "redirect:/profile";
    }
}