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
    public News createNews(NewsDTO newsDTO, String authorUsername, boolean canPublish) {
        News news = new News();
        news.setTitle(newsDTO.getTitle());
        news.setContent(newsDTO.getContent());
        news.setImageUrl(newsDTO.getImageUrl());
        news.setExternalImageUrl(validateExternalImageUrl(newsDTO.getExternalImageUrl()));
        news.setLinkUrl(newsDTO.getLinkUrl());

        // Only allow publishing if the user has the right permissions
        if (canPublish) {
            news.setPublished(newsDTO.isPublished());
        } else {
            news.setPublished(false);
        }

        news.setAuthorUsername(authorUsername);
        return newsRepository.save(news);
    }

    @Transactional
    public News updateNews(Long id, NewsDTO newsDTO, boolean canPublish) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nyhet hittades inte"));
        news.setTitle(newsDTO.getTitle());
        news.setContent(newsDTO.getContent());
        news.setImageUrl(newsDTO.getImageUrl());
        news.setExternalImageUrl(validateExternalImageUrl(newsDTO.getExternalImageUrl()));
        news.setLinkUrl(newsDTO.getLinkUrl());

        // Only allow updating published status if the user has the right permissions
        if (canPublish) {
            news.setPublished(newsDTO.isPublished());
        } else {
            // If they can't publish, and they edit a news, we might want to keep it unpublished
            // or just not allow them to change the published status.
            // Given the requirement "All news created by these roles should be saved as unpublished",
            // we'll force it to false if they are not Admin.
            news.setPublished(false);
        }

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
