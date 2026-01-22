package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByPublishedTrueOrderByCreatedAtDesc();
    List<News> findAllByOrderByCreatedAtDesc();
}