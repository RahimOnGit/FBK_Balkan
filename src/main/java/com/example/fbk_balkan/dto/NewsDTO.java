package com.example.fbk_balkan.dto;

import com.example.fbk_balkan.entity.News;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 10, message = "Content must be at least 10 characters")
    private String content;

    private String author;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

    private boolean published;

    public static NewsDTO fromEntity(News news) {
        if (news == null) return null;
        return NewsDTO.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .author(news.getAuthor())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .published(news.isPublished())
                .build();
    }

    public News toEntity() {
        News news = new News();
        news.setId(this.id);
        news.setTitle(this.title);
        news.setContent(this.content);
        news.setAuthor(this.author);
        news.setCreatedAt(this.createdAt);
        news.setUpdatedAt(this.updatedAt);
        news.setPublished(this.published);
        return news;
    }
}