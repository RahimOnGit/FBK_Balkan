package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.NewsDTO;
import com.example.fbk_balkan.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping
    public String listPublicNews(Model model) {
        model.addAttribute("newsList", newsService.getAllPublishedNews());
        return "news/list";
    }

    @GetMapping("/manage")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String manageNews(Model model) {
        model.addAttribute("newsList", newsService.getAllNews());
        return "news/manage";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("newsDTO", new NewsDTO());
        return "news/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String createNews(
            @Valid @ModelAttribute NewsDTO newsDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "news/create";
        }

        newsService.createNews(newsDTO, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "News created successfully!");
        return "redirect:/news";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        return newsService.getNewsById(id)
                .map(newsDTO -> {
                    model.addAttribute("newsDTO", newsDTO);
                    return "news/edit";
                })
                .orElse("redirect:/news/manage");
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String updateNews(
            @PathVariable Long id,
            @Valid @ModelAttribute NewsDTO newsDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "news/edit";
        }

        newsService.updateNews(id, newsDTO);
        redirectAttributes.addFlashAttribute("successMessage", "News updated successfully!");
        return "redirect:/news/manage";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String deleteNews(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (newsService.deleteNews(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "News deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete news!");
        }
        return "redirect:/news/manage";
    }

    @PostMapping("/unpublish/{id}")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String unpublishNews(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (newsService.unpublishNews(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "News unpublished successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to unpublish news!");
        }
        return "redirect:/news/manage";
    }
}