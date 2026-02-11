package com.example.fbk_balkan.controller;

//import ch.qos.logback.core.model.Model;
import org.springframework.ui.Model;
import com.example.fbk_balkan.dto.CoachCreateUpdateDTO;
import com.example.fbk_balkan.mapper.CoachMapper;
import com.example.fbk_balkan.service.CoachService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/coaches")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCoachController {

    private final CoachService coachService;
    private final CoachMapper coachMapper;

    @GetMapping
    public String listCoaches(Model model) {
        model.addAttribute("coaches", coachService.findAllCoaches());
        return "admin/coaches/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("coach", new CoachCreateUpdateDTO());
        model.addAttribute("isEdit", false);
        return "admin/coaches/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("coach", coachService.getCoachForEdit(id));
        model.addAttribute("isEdit", true);
        return "admin/coaches/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("coach") CoachCreateUpdateDTO dto,
                       BindingResult result,
                       RedirectAttributes ra,
                       Model model) {
        System.out.println("===== POST /admin/coaches/save called =====");
        System.out.println("ID from form: " + dto.getId());
        System.out.println("Email: " + dto.getEmail());
        System.out.println("First name: " + dto.getFirstName());

        if (result.hasErrors()) {
            System.out.println("Validation errors: " + result.getAllErrors());
            model.addAttribute("isEdit", dto.getId() != null);
            return "admin/coaches/form";
        }

        try {
            if (dto.getId() == null) {
                coachService.createCoach(dto);
                ra.addFlashAttribute("success", "Tränare skapad!");
            } else {
                coachService.updateCoach(dto.getId(), dto);
                ra.addFlashAttribute("success", "Tränare uppdaterad!");
            }
            return "redirect:/admin/coaches";
        } catch (ResponseStatusException e) {
            model.addAttribute("error", e.getReason());
            model.addAttribute("isEdit", dto.getId() != null);
            return "admin/coaches/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            coachService.deleteCoach(id);
            ra.addFlashAttribute("success", "Tränare borttagen!");
        } catch (ResponseStatusException e) {
            ra.addFlashAttribute("error", e.getReason());
        }
        return "redirect:/admin/coaches";
    }
}