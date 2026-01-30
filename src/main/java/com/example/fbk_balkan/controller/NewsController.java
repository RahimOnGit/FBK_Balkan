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

    @GetMapping("/news")
    public String publicNewsList(Model model) {
        List<News> newsList = newsService.getAllPublishedNews();
        model.addAttribute("newsList", newsList);
        return "news/list";
    }

    @GetMapping("/news/{id}")
    public String viewNews(@PathVariable Long id, Model model) {
        News news = newsService.getNewsById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));
        if (!news.isPublished()) {
            throw new RuntimeException("Nyhet är inte publicerad");
        }
        model.addAttribute("news", news);
        return "news/view";
    }

    @GetMapping("/admin/news")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String adminNewsList(Model model) {
        List<News> newsList = newsService.getAllNews();
        model.addAttribute("newsList", newsList);
        return "news/admin-list";
    }

    @GetMapping("/admin/news/create")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("newsDTO", new NewsDTO());
        model.addAttribute("isEdit", false);
        return "news/form";
    }

    @PostMapping("/admin/news/create")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String createNews(@Valid @ModelAttribute NewsDTO newsDTO,
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
        newsService.createNews(newsDTO, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Nyhet skapad!");
        return "redirect:/admin/news";
    }

    @GetMapping("/admin/news/edit/{id}")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        News news = newsService.getNewsById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));
        NewsDTO newsDTO = new NewsDTO();
        newsDTO.setId(news.getId());
        newsDTO.setTitle(news.getTitle());
        newsDTO.setContent(news.getContent());
        newsDTO.setImageUrl(news.getImageUrl());
        newsDTO.setExternalImageUrl(news.getExternalImageUrl());
        newsDTO.setLinkUrl(news.getLinkUrl());
        newsDTO.setPublished(news.isPublished());
        model.addAttribute("newsDTO", newsDTO);
        model.addAttribute("isEdit", true);
        model.addAttribute("newsId", id);
        return "news/form";
    }

//    @PostMapping("/admin/news/edit/{id}")
//    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
//    public String updateNews(@PathVariable Long id,
//                             @Valid @ModelAttribute NewsDTO newsDTO,
//                             BindingResult result,
//                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
//                             RedirectAttributes redirectAttributes,
//                             Model model) {
//        if (result.hasErrors()) {
//            model.addAttribute("isEdit", true);
//            model.addAttribute("newsId", id);
//            return "news/form";
//        }
//        if (imageFile != null && !imageFile.isEmpty()) {
//            String imageUrl = fileStorageService.storeFile(imageFile);
//            newsDTO.setImageUrl(imageUrl);
//            newsDTO.setExternalImageUrl(null);
//        }
//        newsService.updateNews(id, newsDTO);
//        redirectAttributes.addFlashAttribute("successMessage", "Nyhet uppdaterad!");
//        return "redirect:/admin/news";
//    }

    @PostMapping("/admin/news/edit/{id}")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String updateNews(
            @PathVariable Long id,
            @Valid @ModelAttribute NewsDTO newsDTO,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "deleteImage", required = false, defaultValue = "false") String deleteImageFlag,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        // 1. Validation errors
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("newsId", id);
            return "news/form";
        }

        // 2. Load existing News from DB (Source of truth)
        News existingNews = newsService.getNewsById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));

        String oldImageUrl = existingNews.getImageUrl();

        // 3. Case A: User uploads a new image
        if (imageFile != null && !imageFile.isEmpty()) {

            // Delete old image if it exists
            if (oldImageUrl != null && !oldImageUrl.isBlank()) {
                fileStorageService.deleteFile(oldImageUrl);
            }

            // Store new image
            String newImageUrl = fileStorageService.storeFile(imageFile);

            // Update DTO with new image
            newsDTO.setImageUrl(newImageUrl);

            // Clear external link preference
            newsDTO.setExternalImageUrl(null);
        }

        // 4. Case B: User requests deletion (only if no new file uploaded)
        else if ("true".equals(deleteImageFlag)) {

            if (oldImageUrl != null && !oldImageUrl.isBlank()) {
                fileStorageService.deleteFile(oldImageUrl);
            }

            // Remove image from DB
            newsDTO.setImageUrl(null);
        }

        // 5. Case C: No upload, no delete
        else {
            // Keep existing image
            newsDTO.setImageUrl(oldImageUrl);
        }

        // 6. Update the news in DB
        newsService.updateNews(id, newsDTO);

        redirectAttributes.addFlashAttribute("successMessage", "Nyhet uppdaterad!");
        return "redirect:/admin/news";
    }


    @PostMapping("/admin/news/delete/{id}")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String deleteNews(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        newsService.deleteNews(id);
        redirectAttributes.addFlashAttribute("successMessage", "Nyhet borttagen!");
        return "redirect:/admin/news";
    }

    @PostMapping("/admin/news/toggle/{id}")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String togglePublished(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        newsService.togglePublished(id);
        redirectAttributes.addFlashAttribute("successMessage", "Publiceringsstatus ändrad!");
        return "redirect:/admin/news";
    }

    @PostMapping("/admin/news/delete-image/{id}")
    @PreAuthorize("hasAnyRole('SOCIAL_ADMIN', 'ADMIN')")
    public String deleteImage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String oldImageUrl = newsService.clearNewsImage(id);
        if (oldImageUrl != null) {
            fileStorageService.deleteFile(oldImageUrl);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Bild borttagen!");
        return "redirect:/admin/news/edit/" + id;
    }
}
