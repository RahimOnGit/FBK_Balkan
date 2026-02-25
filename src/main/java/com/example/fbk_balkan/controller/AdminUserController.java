package com.example.fbk_balkan.controller;

//import ch.qos.logback.core.model.Model;
import com.example.fbk_balkan.dto.UserListItemDTO;
import org.springframework.ui.Model;
import com.example.fbk_balkan.dto.UserCreateUpdateDto;
import com.example.fbk_balkan.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/coaches")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
//    private final UserMapper coachMapper;

//    @GetMapping
//    public String listCoaches(Model model) {
//        model.addAttribute("users", userService.findAllCoaches());
//        return "admin/coaches/list";
//    }

    @GetMapping
    public String listUsers(Model model) {
        List<UserListItemDTO> users = userService.findAllForAdminList();
        System.out.println("Number of users referencing the page: " + users.size());
        model.addAttribute("users", users);
        return "admin/coaches/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("user", new UserCreateUpdateDto());
        model.addAttribute("isEdit", false);
        return "admin/coaches/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        UserCreateUpdateDto dto = userService.getUserForEdit(id);
        model.addAttribute("user", dto);
        model.addAttribute("isEdit", true);
        return "admin/coaches/form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("user") UserCreateUpdateDto dto,
            BindingResult result,
            RedirectAttributes ra,
            Model model) {

        if (result.hasErrors()) {
            // ← This part is very important
            model.addAttribute("user", dto);           // Replace the object in the model
            model.addAttribute("isEdit", dto.getId() != null);

            return "admin/coaches/form";   // ← redirect
        }

        try {
            if (dto.getId() == null) {
                userService.createUser(dto);
                ra.addFlashAttribute("success", "User created successfully");
            } else {
                userService.updateUser(dto.getId(), dto);
                ra.addFlashAttribute("success", "The user was successfully modified");
            }
            return "redirect:/admin/coaches";
        } catch (Exception e) {
            // In case of a logical error (such as a duplicate email)
            model.addAttribute("user", dto);
            model.addAttribute("isEdit", dto.getId() != null);
            model.addAttribute("error", "An error occurred: " + e.getMessage());

            return "admin/coaches/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.deleteUser(id);
            ra.addFlashAttribute("success", "Tränare borttagen!");
        } catch (ResponseStatusException e) {
            ra.addFlashAttribute("error", e.getReason());
        }
        return "redirect:/admin/coaches";
    }
}