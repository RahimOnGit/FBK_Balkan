package com.example.fbk_balkan.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SitemapController {

    @GetMapping(value = "/sitemap.xml", produces = "application/xml")
    public ResponseEntity<ClassPathResource> sitemap() {
        ClassPathResource resource = new ClassPathResource("static/sitemap.xml");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(resource);
    }
}