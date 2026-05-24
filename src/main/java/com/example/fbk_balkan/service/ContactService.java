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

    @Autowired
    private JavaMailSender mailSender;

    @Value("${contact.admin.email:info@fbkbalkan.se}")
    private String adminEmail;

    @Async
    public void sendContactEmail(ContactFormDTO dto) {
        if (mailSender == null) {
            log.warn("E-postserver ej konfigurerad.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(adminEmail);
            message.setTo(adminEmail);
            message.setReplyTo(dto.getEmail());
            message.setSubject("🔵 Ny kontaktförfrågan: " + dto.getSubject());

            String body = """
                    Ny meddelande via kontaktformuläret på FBK Balkan

                    Namn:    %s
                    E-post:  %s
                    Telefon: %s
                    Ämne:    %s

                    Meddelande:
                    ───────────────────────────────────────────
                    %s
                    ───────────────────────────────────────────

                    Skickat: %s
                    """.formatted(
                    dto.getName(),
                    dto.getEmail(),
                    dto.getPhone() != null && !dto.getPhone().isBlank() ? dto.getPhone() : "(ej angivet)",
                    dto.getSubject(),
                    dto.getMessage(),
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );

            message.setText(body);

            mailSender.send(message);
            log.info("✅ Kontaktmejl skickat asynkront till {}", adminEmail);

        } catch (Exception e) {
            log.error("❌ Kunde inte skicka kontaktmejl", e);
        }
    }
}