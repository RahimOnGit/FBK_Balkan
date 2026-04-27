//package com.example.fbk_balkan.controller;
//
//import com.example.fbk_balkan.dto.UserProfileUpdateDto;
//import com.example.fbk_balkan.dto.UserProfileViewDto;
//import com.example.fbk_balkan.service.UserService;
//import jakarta.validation.Valid;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//public class ProfileController {
//
//    private final UserService userService;
//
//    public ProfileController(UserService userService) {
//        this.userService = userService;
//    }
//
//    // =========================
//    // GET PROFILE
//    // =========================
//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/profile")
//    public String profile(Model model,
//                          @AuthenticationPrincipal UserDetails userDetails) {
//
//        UserProfileViewDto dto =
//                userService.getCurrentUserProfile(userDetails.getUsername());
//
//        model.addAttribute("user", dto);
//        // pre-fill phone form
//        UserProfileUpdateDto phoneForm = new UserProfileUpdateDto();
//        phoneForm.setPhone(dto.getPhone());
//
//        model.addAttribute("phoneForm", phoneForm);
//        return "private-pages/profile";
//    }
//
//
//    @PreAuthorize("isAuthenticated()")
//    @PostMapping("/profile/phone")
//    public String updatePhone(
//            @ModelAttribute("phoneForm") @Valid UserProfileUpdateDto dto,
//            BindingResult result,
//            @AuthenticationPrincipal UserDetails userDetails,
//            Model model,
//            RedirectAttributes ra) {
//
//        if (result.hasErrors()) {
//
//            UserProfileViewDto user =
//                    userService.getCurrentUserProfile(userDetails.getUsername());
//
//            model.addAttribute("user", user);
//
//            //  keep value in form
//            UserProfileUpdateDto phoneForm = new UserProfileUpdateDto();
//            phoneForm.setPhone(dto.getPhone());
//
//            model.addAttribute("phoneForm", phoneForm);
//
//            return "private-pages/profile";
//        }
//
//        userService.updatePhone(userDetails.getUsername(), dto.getPhone());
//
//        ra.addFlashAttribute("success", "Telefon uppdaterad!");
//        return "redirect:/profile";
//    }
//}
package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.ChangePasswordDto;
import com.example.fbk_balkan.dto.UserProfileUpdateDto;
import com.example.fbk_balkan.dto.UserProfileViewDto;
import com.example.fbk_balkan.security.CustomUserDetails;
import com.example.fbk_balkan.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // GET PROFILE
    // =========================

    @GetMapping("/profile")
    public String profile(Model model,
                          @AuthenticationPrincipal CustomUserDetails user) {

        Long userId = user.getId();

        // ONE DB CALL ONLY
        UserProfileViewDto profile = userService.getCurrentUserProfile(userId);

        model.addAttribute("user", profile);

        // prefill forms from SAME object
        UserProfileUpdateDto phoneForm = new UserProfileUpdateDto();
        phoneForm.setPhone(profile.getPhone());

        model.addAttribute("phoneForm", phoneForm);
        model.addAttribute("passwordForm", new ChangePasswordDto());

        return "private-pages/profile";
    }
    // =========================
    // UPDATE PHONE
    // =========================
    @PostMapping("/profile/phone/update")
    public String updatePhone(
            @ModelAttribute("phoneForm") @Valid UserProfileUpdateDto dto,
            BindingResult result,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model,
            RedirectAttributes ra) {

        Long userId = user.getId();

        if (result.hasErrors()) {
            populateModel(model, userId);
            model.addAttribute("phoneForm", dto);
            return "private-pages/profile";
        }

        userService.updatePhone(userId, dto.getPhone());

        ra.addFlashAttribute("success", "Telefon uppdaterad!");
        return "redirect:/profile";
    }

    // =========================
    // UPDATE PASSWORD
    // =========================
    @PostMapping("/profile/password/update")
    public String updatePassword(
            @ModelAttribute("passwordForm") @Valid ChangePasswordDto dto,
            BindingResult result,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model,
            RedirectAttributes ra) {

        Long userId = user.getId();

        if (result.hasErrors()) {
            populateModel(model, userId);
            model.addAttribute("passwordForm", dto);
            return "private-pages/profile";
        }

        try {
            userService.changePassword(userId, dto);

            ra.addFlashAttribute("success", "Lösenord uppdaterat!");
            return "redirect:/profile";

        } catch (IllegalArgumentException e) {

            populateModel(model, userId);

            BindingResult newResult = new BeanPropertyBindingResult(dto, "passwordForm");
            newResult.rejectValue("currentPassword", "error.password", e.getMessage());

            model.addAttribute("org.springframework.validation.BindingResult.passwordForm", newResult);
            model.addAttribute("passwordForm", dto);

            return "private-pages/profile";
        }
    }

    // =========================
    // HELPER METHOD
    // =========================
    private void populateModel(Model model, Long userId) {
        UserProfileViewDto profile = userService.getCurrentUserProfile(userId);

        model.addAttribute("user", profile);
        model.addAttribute("phoneForm", new UserProfileUpdateDto());
        model.addAttribute("passwordForm", new ChangePasswordDto());
    }
}