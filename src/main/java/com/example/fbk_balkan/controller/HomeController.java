package com.example.fbk_balkan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

// view page for /team-register
    @GetMapping("/team-register")
    public String teamRegister() {
        return "private-pages/team-register";
    }


}