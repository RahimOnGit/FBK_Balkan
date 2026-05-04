package com.example.fbk_balkan.controller;


import com.example.fbk_balkan.dto.match.GameDTO;
import com.example.fbk_balkan.entity.News;
import com.example.fbk_balkan.repository.FaqRepository;
import com.example.fbk_balkan.service.MatchService;
import com.example.fbk_balkan.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.List;


@Controller
public class HomeController {


    private final NewsService newsService;
    private final FaqRepository faqRepository;
    private final MatchService matchService;




    public HomeController(NewsService newsService, FaqRepository faqRepository, MatchService matchService) {
        this.newsService = newsService;
        this.faqRepository = faqRepository;
        this.matchService = matchService;
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
//      fetch Match results
        List<GameDTO> matches = matchService.fetchMatches();
        model.addAttribute("matches" , matches);
        model.addAttribute("upcomingMatches", matchService.fetchUpcomingMatchesWithinMonths(2));
        return "index";
    }


    @GetMapping("/matcher")
    public String matcher(Model model) {
        List<GameDTO> upcoming = matchService.fetchUpcomingMatchesWithinMonths(2);
        List<GameDTO> recent = matchService.fetchRecentResultsWithinMonths(2);
        model.addAttribute("upcomingMatches", upcoming);
        model.addAttribute("pastMatches", recent);
        model.addAttribute("upcomingByMonth", matchService.groupByMonth(upcoming));
        model.addAttribute("recentByMonth", matchService.groupByMonth(recent));
        return "matches";
    }


    @GetMapping("/about")
    public String about() {
        return "about";
    }


    @GetMapping("/ungdomsportalen")
    public String ungdomsportalen() {
        return "ungdomsportalen";
    }


    @GetMapping("/verksamhet")
    public String verksamhet() {
        return "verksamhet";
    }
}