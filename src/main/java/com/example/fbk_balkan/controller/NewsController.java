package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.NewsDTO;
import com.example.fbk_balkan.entity.News;
import com.example.fbk_balkan.service.FileStorageService;
import com.example.fbk_balkan.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private FileStorageService fileStorageService;

    // =====================
    // PUBLIC
    // =====================

    @GetMapping("/news")
    public String publicNewsList(Model model) {
        List<News> newsList = newsService.getAllPublishedNews();
        model.addAttribute("newsList", newsList);
        return "news/list";
    }

    @GetMapping("/news/{id}")
    public String viewNews(@PathVariable Long id, Model model, Authentication authentication) {
        News news = newsService.getNewsById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));

        boolean canViewUnpublished = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                                || a.getAuthority().equals("ROLE_COACH")
                                || a.getAuthority().equals("ROLE_SOCIAL_ADMIN"));

        if (!news.isPublished() && !canViewUnpublished) {
            throw new RuntimeException("Nyhet är inte publicerad");
        }

        model.addAttribute("news", news);
        return "news/view";
    }

    // =====================
    // ADMIN
    // =====================

    @GetMapping("/admin/news")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN', 'COACH')")
    public String adminNewsList(Model model) {
        List<News> newsList = newsService.getAllNews();
        model.addAttribute("newsList", newsList);
        model.addAttribute("isCoachView", false);
        return "news/admin-list";
    }

    @GetMapping("/admin/news/create")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN', 'COACH')")
    public String showCreateForm(Model model) {
        model.addAttribute("newsDTO", new NewsDTO());
        model.addAttribute("isEdit", false);
        return "news/form";
    }

    @PostMapping("/admin/news/create")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN', 'COACH')")
    public String createNews(
            @Valid @ModelAttribute NewsDTO newsDTO,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "news/form";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(imageFile);
            newsDTO.setImageUrl(imageUrl);
            newsDTO.setExternalImageUrl(null);
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        newsService.createNews(newsDTO, authentication.getName(), isAdmin);
        redirectAttributes.addFlashAttribute("successMessage",
                isAdmin ? "Nyhet skapad!" : "Nyhet skapad och väntar på granskning!");
        return "redirect:/admin/news";
    }

    @GetMapping("/admin/news/edit/{id}")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN', 'COACH')")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        News news = newsService.getNewsById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !news.getAuthorUsername().equals(authentication.getName())) {
            throw new RuntimeException("Du har inte behörighet att redigera denna nyhet");
        }

        NewsDTO newsDTO = buildNewsDTO(news);
        model.addAttribute("newsDTO", newsDTO);
        model.addAttribute("isEdit", true);
        model.addAttribute("newsId", id);
        model.addAttribute("isCoachView", false);
        return "news/form";
    }

    @PostMapping("/admin/news/edit/{id}")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN', 'COACH')")
    public String updateNews(
            @PathVariable Long id,
            @Valid @ModelAttribute NewsDTO newsDTO,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "deleteImage", required = false, defaultValue = "false") String deleteImageFlag,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("newsId", id);
            model.addAttribute("isCoachView", false);
            return "news/form";
        }

        News existingNews = newsService.getNewsById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !existingNews.getAuthorUsername().equals(authentication.getName())) {
            throw new RuntimeException("Du har inte behörighet att redigera denna nyhet");
        }

        handleImageUpdate(newsDTO, imageFile, deleteImageFlag, existingNews.getImageUrl());

        newsService.updateNews(id, newsDTO, isAdmin);
        redirectAttributes.addFlashAttribute("successMessage",
                isAdmin ? "Nyhet uppdaterad!" : "Nyhet uppdaterad och väntar på granskning!");
        return "redirect:/admin/news";
    }

    @PostMapping("/admin/news/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    public String deleteNews(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        News news = newsService.getNewsById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !news.getAuthorUsername().equals(authentication.getName())) {
            throw new RuntimeException("Du har inte behörighet att ta bort denna nyhet");
        }

        newsService.deleteNews(id);
        redirectAttributes.addFlashAttribute("successMessage", "Nyhet borttagen!");
        return "redirect:/admin/news";
    }

    @PostMapping("/admin/news/toggle/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String togglePublished(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        newsService.togglePublished(id);
        redirectAttributes.addFlashAttribute("successMessage", "Publiceringsstatus ändrad!");
        return "redirect:/admin/news";
    }

    @PostMapping("/admin/news/delete-image/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteImage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String oldImageUrl = newsService.clearNewsImage(id);
        if (oldImageUrl != null) {
            fileStorageService.deleteFile(oldImageUrl);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Bild borttagen!");
        return "redirect:/admin/news/edit/" + id;
    }

    // =====================
    // COACH
    // =====================

    @GetMapping("/coach/news")
    @PreAuthorize("hasRole('COACH')")
    public String coachNewsList(Model model, Authentication authentication) {
        List<News> newsList = newsService.getAllNews();
        List<News> coachNews = newsList.stream()
                .filter(n -> n.getAuthorUsername().equals(authentication.getName()))
                .toList();
        model.addAttribute("newsList", coachNews);
        model.addAttribute("isCoachView", true);
        return "news/admin-list";
    }

    @GetMapping("/coach/news/create")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public String showCoachCreateForm(Model model) {
        model.addAttribute("newsDTO", new NewsDTO());
        model.addAttribute("isEdit", false);
        model.addAttribute("isCoachView", true);
        return "coach/news/form";
    }

    @PostMapping("/coach/news/create")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public String createCoachNews(
            @Valid @ModelAttribute NewsDTO newsDTO,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("isCoachView", true);
            return "coach/news/form";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(imageFile);
            newsDTO.setImageUrl(imageUrl);
            newsDTO.setExternalImageUrl(null);
        }

        newsService.createNews(newsDTO, authentication.getName(), false);
        redirectAttributes.addFlashAttribute("successMessage", "Nyhet skapad och väntar på granskning!");
        return "redirect:/coach/news";
    }

    @GetMapping("/coach/news/edit/{id}")
    @PreAuthorize("hasRole('COACH')")
    public String showCoachEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        News news = newsService.getNewsById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));

        if (!news.getAuthorUsername().equals(authentication.getName())) {
            throw new RuntimeException("Du har inte behörighet att redigera denna nyhet");
        }

        NewsDTO newsDTO = buildNewsDTO(news);
        model.addAttribute("newsDTO", newsDTO);
        model.addAttribute("isEdit", true);
        model.addAttribute("newsId", id);
        model.addAttribute("isCoachView", true);
        return "coach/news/form";
    }

    @PostMapping("/coach/news/edit/{id}")
    @PreAuthorize("hasRole('COACH')")
    public String updateCoachNews(
            @PathVariable Long id,
            @Valid @ModelAttribute NewsDTO newsDTO,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "deleteImage", required = false, defaultValue = "false") String deleteImageFlag,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("newsId", id);
            model.addAttribute("isCoachView", true);
            return "coach/news/form";
        }

        News existingNews = newsService.getNewsById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));

        if (!existingNews.getAuthorUsername().equals(authentication.getName())) {
            throw new RuntimeException("Du har inte behörighet att redigera denna nyhet");
        }

        handleImageUpdate(newsDTO, imageFile, deleteImageFlag, existingNews.getImageUrl());

        newsService.updateNews(id, newsDTO, false); // Coach cannot publish
        redirectAttributes.addFlashAttribute("successMessage", "Nyhet uppdaterad och väntar på granskning!");
        return "redirect:/coach/news";
    }

    @PostMapping("/coach/news/delete/{id}")
    @PreAuthorize("hasRole('COACH')")
    public String deleteCoachNews(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        News news = newsService.getNewsById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));

        if (!news.getAuthorUsername().equals(authentication.getName())) {
            throw new RuntimeException("Du har inte behörighet att ta bort denna nyhet");
        }

        newsService.deleteNews(id);
        redirectAttributes.addFlashAttribute("successMessage", "Nyhet borttagen!");
        return "redirect:/coach/news";
    }

    // =====================
    // HELPERS
    // =====================

    private NewsDTO buildNewsDTO(News news) {
        NewsDTO newsDTO = new NewsDTO();
        newsDTO.setId(news.getId());
        newsDTO.setTitle(news.getTitle());
        newsDTO.setContent(news.getContent());
        newsDTO.setImageUrl(news.getImageUrl());
        newsDTO.setExternalImageUrl(news.getExternalImageUrl());
        newsDTO.setLinkUrl(news.getLinkUrl());
        newsDTO.setPublished(news.isPublished());
        return newsDTO;
    }

    private void handleImageUpdate(NewsDTO newsDTO, MultipartFile imageFile,
                                   String deleteImageFlag, String oldImageUrl) {
        if (imageFile != null && !imageFile.isEmpty()) {
            if (oldImageUrl != null && !oldImageUrl.isBlank()) {
                fileStorageService.deleteFile(oldImageUrl);
            }
            newsDTO.setImageUrl(fileStorageService.storeFile(imageFile));
            newsDTO.setExternalImageUrl(null);
        } else if ("true".equals(deleteImageFlag)) {
            if (oldImageUrl != null && !oldImageUrl.isBlank()) {
                fileStorageService.deleteFile(oldImageUrl);
            }
            newsDTO.setImageUrl(null);
        } else {
            newsDTO.setImageUrl(oldImageUrl);
        }
    }
}