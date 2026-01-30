package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.NewsDTO;
import com.example.fbk_balkan.entity.News;
import com.example.fbk_balkan.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    public List<News> getAllPublishedNews() {
        return newsRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    public List<News> getAllNews() {
        return newsRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<News> getNewsById(Long id) {
        return newsRepository.findById(id);
    }

    @Transactional
    public News createNews(NewsDTO newsDTO, String authorUsername) {
        News news = new News();
        news.setTitle(newsDTO.getTitle());
        news.setContent(newsDTO.getContent());
        news.setImageUrl(newsDTO.getImageUrl());
        news.setExternalImageUrl(validateExternalImageUrl(newsDTO.getExternalImageUrl()));
        news.setLinkUrl(newsDTO.getLinkUrl());
        news.setPublished(newsDTO.isPublished());
        news.setAuthorUsername(authorUsername);
        return newsRepository.save(news);
    }

    @Transactional
    public News updateNews(Long id, NewsDTO newsDTO) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));
        news.setTitle(newsDTO.getTitle());
        news.setContent(newsDTO.getContent());
        news.setImageUrl(newsDTO.getImageUrl());
        news.setExternalImageUrl(validateExternalImageUrl(newsDTO.getExternalImageUrl()));
        news.setLinkUrl(newsDTO.getLinkUrl());
        news.setPublished(newsDTO.isPublished());
        return newsRepository.save(news);
    }

    private String validateExternalImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        String trimmed = url.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        return null;
    }

    @Transactional
    public String clearNewsImage(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));
        String oldImageUrl = news.getImageUrl();
        news.setImageUrl(null);
        newsRepository.save(news);
        return oldImageUrl;
    }

    @Transactional
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }

    @Transactional
    public void togglePublished(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));
        news.setPublished(!news.isPublished());
        newsRepository.save(news);
    }
}
