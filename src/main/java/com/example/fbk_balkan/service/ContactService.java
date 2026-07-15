package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.ContactFormDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service
@Slf4j
public class ContactService {

    private final EmailService emailService;
    private final String adminEmail;

    public ContactService(EmailService emailService,
                          @Value("${contact.admin.email:ungdom@fbkbalkan.se}") String adminEmail) {
        this.emailService = emailService;
        this.adminEmail = adminEmail;
    }

    @Async
    public void sendContactEmail(ContactFormDTO dto) {
        try {
            emailService.sendContactForm(adminEmail, dto);
        } catch (Exception e) {
            log.error("Kunde inte skicka kontaktmejl: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}