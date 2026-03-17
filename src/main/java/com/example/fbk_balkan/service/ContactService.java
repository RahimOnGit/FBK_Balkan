package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.ContactFormDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContactService {

//    @Autowired(required = false)
    @Autowired
    private JavaMailSender mailSender;

    @Value("${contact.admin.email:admin@fbkbalkan.se}")
    private String adminEmail;

    public void sendContactEmail(ContactFormDTO dto) {
        if (mailSender == null) {
            log.warn("E-postserver ej konfigurerad. Kontaktformulär inskickat av: {} – {}",
                    dto.getName(), dto.getEmail());
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject("Kontaktformulär: " + dto.getSubject());
            message.setReplyTo(dto.getEmail());
            message.setText(
                    "Nytt meddelande via kontaktformuläret på fbkbalkan.se\n" +
                            "═══════════════════════════════════════════\n\n" +
                            "Namn:    " + dto.getName() + "\n" +
                            "E-post:  " + dto.getEmail() + "\n" +
                            (dto.getPhone() != null && !dto.getPhone().isBlank()
                                    ? "Telefon: " + dto.getPhone() + "\n" : "") +
                            "Ämne:    " + dto.getSubject() + "\n\n" +
                            "Meddelande:\n" +
                            "───────────────────────────────────────────\n" +
                            dto.getMessage() + "\n" +
                            "───────────────────────────────────────────\n"
            );
            mailSender.send(message);
            log.info("Kontaktformulär skickat från: {}", dto.getEmail());
        } catch (Exception e) {
            log.error("Kunde inte skicka kontaktformuläret via e-post", e);
        }
    }
}