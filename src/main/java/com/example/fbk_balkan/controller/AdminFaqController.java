
package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.FaqFormDTO;
import com.example.fbk_balkan.service.FaqService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/faqs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFaqController {

    private final FaqService faqService;

    public AdminFaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("faqs", faqService.getAllFaqs());
        return "admin/faqs/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("faq", new FaqFormDTO());
        return "admin/faqs/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       Model model,
                       RedirectAttributes redirectAttributes) {

        try {

            var faq = faqService.getById(id);

            FaqFormDTO dto = new FaqFormDTO();
            dto.setId(faq.getId());
            dto.setQuestion(faq.getQuestion());
            dto.setAnswer(faq.getAnswer());
            dto.setDisplayOrder(faq.getDisplayOrder());
            dto.setVisible(faq.isVisible());

            model.addAttribute("faq", dto);

            return "admin/faqs/form";

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("error",
                    "FAQ hittades inte");

            return "redirect:/admin/faqs";
        }
    }


    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("faq") FaqFormDTO faq,
                       BindingResult result,
                       RedirectAttributes redirectAttributes,
                       Model model) {

        // Validation errors (field errors)
        if (result.hasErrors()) {
            return "admin/faqs/form";
        }

        try {
            faqService.save(faq);

            // Check if this is a new FAQ or an update
            if (faq.getId() == null) {
                redirectAttributes.addFlashAttribute("success", "FAQ skapad!");
            } else {
                redirectAttributes.addFlashAttribute("success", "FAQ uppdaterad!");
            }

            return "redirect:/admin/faqs";

        } catch (Exception e) {
            // Logical / other errors
            model.addAttribute("error", "Ett fel uppstod: " + e.getMessage());
            return "admin/faqs/form";
        }
    }
@PostMapping("/delete/{id}")
public String delete(@PathVariable Long id,
                     RedirectAttributes redirectAttributes) {

    try {

        faqService.delete(id);

        redirectAttributes.addFlashAttribute("success", "FAQ borttagen!");

    } catch (Exception e) {

        redirectAttributes.addFlashAttribute("error",
                "Kunde inte ta bort FAQ");
    }

    return "redirect:/admin/faqs";
}
}