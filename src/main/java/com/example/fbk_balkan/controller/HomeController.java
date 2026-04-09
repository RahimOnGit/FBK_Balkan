package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.entity.News;
import com.example.fbk_balkan.repository.FaqRepository;
import com.example.fbk_balkan.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final NewsService newsService;
    private final FaqRepository faqRepository;

    public HomeController(NewsService newsService,FaqRepository faqRepository) {
        this.newsService = newsService;
        this.faqRepository = faqRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<News> allPublished = newsService.getAllPublishedNews();
        News latestSingleNews = allPublished.isEmpty() ? null : allPublished.get(0);
        List<News> recentNews = allPublished.size() > 3 ? allPublished.subList(0, 3) : allPublished;
        model.addAttribute("latestNews", latestSingleNews);
        model.addAttribute("recentNews", recentNews);
        model.addAttribute(
                "homeFaqs",
                faqRepository.findTop3ByVisibleTrueOrderByDisplayOrderAsc()
        );
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/ungdomsportalen")
    public String ungdomsportalen() {
        return "ungdomsportalen";
    }

}