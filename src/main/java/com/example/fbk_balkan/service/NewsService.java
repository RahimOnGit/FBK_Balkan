package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.NewsDTO;
import com.example.fbk_balkan.entity.News;
import com.example.fbk_balkan.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    public List<NewsDTO> getAllPublishedNews() {
        return newsRepository.findByPublishedTrueOrderByCreatedAtDesc()
                .stream()
                .map(NewsDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<NewsDTO> getAllNews() {
        return newsRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(NewsDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<NewsDTO> getNewsById(Long id) {
        return newsRepository.findById(id)
                .map(NewsDTO::fromEntity);
    }

    public NewsDTO createNews(NewsDTO newsDTO, String authorUsername) {
        News news = newsDTO.toEntity();
        news.setAuthor(authorUsername);
        news.setCreatedAt(LocalDateTime.now());
        news.setPublished(true);
        News savedNews = newsRepository.save(news);
        return NewsDTO.fromEntity(savedNews);
    }

    public NewsDTO updateNews(Long id, NewsDTO newsDTO) {
        return newsRepository.findById(id)
                .map(existingNews -> {
                    existingNews.setTitle(newsDTO.getTitle());
                    existingNews.setContent(newsDTO.getContent());
                    existingNews.setUpdatedAt(LocalDateTime.now());
                    News updatedNews = newsRepository.save(existingNews);
                    return NewsDTO.fromEntity(updatedNews);
                })
                .orElse(null);
    }

    public boolean deleteNews(Long id) {
        if (newsRepository.existsById(id)) {
            newsRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean unpublishNews(Long id) {
        return newsRepository.findById(id)
                .map(news -> {
                    news.setPublished(false);
                    newsRepository.save(news);
                    return true;
                })
                .orElse(false);
    }
}