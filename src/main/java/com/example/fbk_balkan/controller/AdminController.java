package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.UserResponseDto;
import com.example.fbk_balkan.dto.UserCreateUpdateDto;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // Show the registration form
    @GetMapping("/register-coach-form")
    public String showRegisterUserForm(Model model) {
        model.addAttribute("userDto", new UserCreateUpdateDto());
        return "register-coach-form";
    }

    // Handle form submission
    @PostMapping("/register-coach")
    public String createCoach(
            @Valid @ModelAttribute UserCreateUpdateDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return "register-coach-form";
        }

        try {
            // Create the coach
            User createdUser = userService.createUser(dto);

            // Add success message
            redirectAttributes.addFlashAttribute("successMessage",
                    "Tränare " + createdUser.getFirstName() + " " + createdUser.getLastName() +
                            " har registrerats framgångsrikt!");

            // Redirect to form again or to a list page
            return "redirect:/admin/register-coach-form";

        } catch (Exception e) {
            // Handle errors (e.g., duplicate email)
            model.addAttribute("errorMessage",
                    "Kunde inte registrera tränare: " + e.getMessage());
            return "register-coach-form";
        }
    }

    // API endpoint for JSON requests (if you want to keep it for Postman testing)
    @PostMapping("/register-coach-api")
    @ResponseBody
    public UserResponseDto createCoachApi(@RequestBody UserCreateUpdateDto dto) {
        User coach = userService.createUser(dto);

        UserResponseDto res = new UserResponseDto();
        res.setId(coach.getId());
        res.setFirstName(coach.getFirstName());
        res.setLastName(coach.getLastName());
        res.setEmail(coach.getEmail());
        res.setRole(coach.getRole());
        return res;
    }
}