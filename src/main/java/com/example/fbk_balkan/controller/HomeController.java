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

import com.example.fbk_balkan.entity.News;
import com.example.fbk_balkan.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final NewsService newsService;

    public HomeController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<News> latestNews = newsService.getAllPublishedNews();
        News latestSingleNews = latestNews.isEmpty() ? null : latestNews.get(0);
        model.addAttribute("latestNews", latestSingleNews);
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }




}