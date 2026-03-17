package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.entity.Faq;
import com.example.fbk_balkan.service.FaqService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping("/faq")
    public String faqPage(Model model) {

        // Get FAQs from service
        List<Faq> faqs = faqService.getVisibleFaqs();

        model.addAttribute("faqs", faqs);

        // Find latest update date
        Optional<LocalDateTime> latestUpdated = faqs.stream()
                .map(Faq::getUpdatedAt)
                .max(LocalDateTime::compareTo);

        model.addAttribute("faqLastUpdated", latestUpdated.orElse(null));

        return "faq";
    }
}