//package com.example.fbk_balkan.controller;
//
//import com.example.fbk_balkan.entity.News;
//import com.example.fbk_balkan.service.NewsService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.util.List;
//
//@Controller
//public class HomeController {
//
//    private final NewsService newsService;
//
//    public HomeController(NewsService newsService) {
//        this.newsService = newsService;
//    }
//
//    @GetMapping("/")
//    public String home(Model model) {
//        List<News> latestNews = newsService.getAllPublishedNews();
//        if (latestNews.size() > 3) {
//            latestNews = latestNews.subList(0, 3);
//        }
//        model.addAttribute("latestNews", latestNews);
//        return "index";
//    }
//
//    @GetMapping("/about")
//    public String about() {
//        return "about";
//    }
//
//
//
//
//}

package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.match.GameDTO;
import com.example.fbk_balkan.entity.News;
import com.example.fbk_balkan.repository.FaqRepository;
import com.example.fbk_balkan.repository.MatchRepository;
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

    public HomeController(NewsService newsService, FaqRepository faqRepository, MatchRepository matchRepository, MatchService matchService) {
        this.newsService = newsService;
        this.faqRepository = faqRepository;
        this.matchService = matchService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<News> latestNews = newsService.getAllPublishedNews();
        News latestSingleNews = latestNews.isEmpty() ? null : latestNews.get(0);
//      fetch last news
        model.addAttribute("latestNews", latestSingleNews);
//     fetch   FAQ
        model.addAttribute(
                "homeFaqs",
                faqRepository.findTop3ByVisibleTrueOrderByDisplayOrderAsc()
        );
//      fetch Match results
        List<GameDTO> matches = matchService.fetchMatches();
        model.addAttribute("matches" , matches);
        System.out.println("NEW Match results: " + matchService.fetchMatches());
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }



}