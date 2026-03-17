package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    List<Faq> findByVisibleTrueOrderByDisplayOrderAsc();

    List<Faq> findTop3ByVisibleTrueOrderByDisplayOrderAsc();

}