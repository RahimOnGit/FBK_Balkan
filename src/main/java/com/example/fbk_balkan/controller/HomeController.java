package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.match.GameDTO;
import com.example.fbk_balkan.entity.News;
import com.example.fbk_balkan.repository.FaqRepository;
import com.example.fbk_balkan.service.MatchService;
import com.example.fbk_balkan.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String matcher(
            @RequestParam(name = "view", required = false, defaultValue = "upcoming") String view,
            @RequestParam(name = "competition", required = false) String competition,
            @RequestParam(name = "team", required = false) String team,
            @RequestParam(name = "q", required = false) String q,
            Model model
    ) {
        // Normalize view to a known value
        String safeView = switch (view == null ? "" : view.toLowerCase()) {
            case "results", "all", "upcoming" -> view.toLowerCase();
            default -> "upcoming";
        };

        List<GameDTO> filtered = matchService.filterMatches(safeView, competition, team, q);

        model.addAttribute("matches", filtered);
        model.addAttribute("matchesByMonth", matchService.groupByMonth(filtered));
        model.addAttribute("totalCount", filtered.size());

        // Filter options
        model.addAttribute("competitions", matchService.getDistinctCompetitions());
        model.addAttribute("fbkTeams", matchService.getDistinctFbkTeams());

        // Echo selected filters for the form
        model.addAttribute("selectedView", safeView);
        model.addAttribute("selectedCompetition", competition);
        model.addAttribute("selectedTeam", team);
        model.addAttribute("searchQuery", q);

        // True iff any filter is active (used to show "Rensa filter")
        boolean hasFilters = (competition != null && !competition.isBlank())
                || (team != null && !team.isBlank())
                || (q != null && !q.isBlank())
                || !"upcoming".equals(safeView);
        model.addAttribute("hasFilters", hasFilters);

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
