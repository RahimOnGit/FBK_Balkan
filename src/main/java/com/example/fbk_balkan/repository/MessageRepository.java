package com.example.fbk_balkan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fbk_balkan.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}

