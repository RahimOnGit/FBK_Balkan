package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.entity.News;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.repository.UserRepository;
import com.example.fbk_balkan.service.NewsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SocialAdminDashboardController {

    private final NewsService newsService;
    private final UserRepository userRepository;

    public SocialAdminDashboardController(NewsService newsService, UserRepository userRepository) {
        this.newsService = newsService;
        this.userRepository = userRepository;
    }

    @GetMapping("/socialadmin/dashboard")
    @PreAuthorize("hasRole('SOCIAL_ADMIN')")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElse(null);

        String displayName = user != null ? user.getFirstName() + " " + user.getLastName() : "Social Admin";
        model.addAttribute("displayName", displayName);

        List<News> allNews = newsService.getAllNews();
        long totalNews = allNews.size();
        long publishedCount = allNews.stream().filter(News::isPublished).count();
        long draftCount = totalNews - publishedCount;
        long myNewsCount = allNews.stream()
                .filter(n -> email.equals(n.getAuthorUsername()))
                .count();

        model.addAttribute("totalNews", totalNews);
        model.addAttribute("publishedCount", publishedCount);
        model.addAttribute("draftCount", draftCount);
        model.addAttribute("myNewsCount", myNewsCount);

        List<News> recentNews = allNews.stream().limit(5).toList();
        model.addAttribute("recentNews", recentNews);

        return "socialadmin/dashboard";
    }
}
