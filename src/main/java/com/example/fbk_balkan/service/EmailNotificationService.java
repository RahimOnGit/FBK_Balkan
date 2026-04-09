package com.example.fbk_balkan.service;

import com.example.fbk_balkan.entity.News;
import com.example.fbk_balkan.entity.Sponsor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${contact.admin.email:}")
    private String adminEmail;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void sendNewsPublishedNotification(News news, String publishedByUsername) {
        if (mailSender == null || adminEmail == null || adminEmail.isBlank()) {
            log.warn("Mail not configured. Skipping news published notification.");
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("FBK Balkan – Ny nyhet publicerad");
            message.setText(
                    "Hej!\n\n" +
                            "En ny nyhet har publicerats på FBK Balkan webbplatsen.\n\n" +
                            "Titel: " + news.getTitle() + "\n" +
                            "Publicerad av: " + (news.getAuthorFullName() != null ? news.getAuthorFullName() : publishedByUsername) + "\n" +
                            "Tidpunkt: " + (news.getUpdatedAt() != null ? news.getUpdatedAt().toString() : "just nu") + "\n\n" +
                            "Logga in på adminpanelen för att granska nyheten.\n\n" +
                            "Med vänliga hälsningar,\nFBK Balkan System"
            );
            mailSender.send(message);
            log.info("News published notification sent to {} for news '{}'", adminEmail, news.getTitle());
        } catch (Exception e) {
            log.error("Failed to send news published notification: {}", e.getMessage());
        }
    }

    public void sendSponsorExpiryNotification(List<Sponsor> expiringSponsors, int daysUntilExpiry) {
        if (mailSender == null || adminEmail == null || adminEmail.isBlank()) {
            log.warn("Mail not configured. Skipping sponsor expiry notification.");
            return;
        }
        if (expiringSponsors.isEmpty()) return;

        try {
            StringBuilder body = new StringBuilder();
            body.append("Hej!\n\n");
            body.append("Följande sponsoravtal löper ut inom ").append(daysUntilExpiry).append(" dagar och behöver förnyas:\n\n");

            for (Sponsor s : expiringSponsors) {
                body.append("- ").append(s.getName())
                        .append(" (").append(s.getCategory().getDisplayName()).append(")")
                        .append(" – Avtal slutar: ").append(s.getAgreementEnd().format(DATE_FORMAT));
                if (s.getContactEmail() != null && !s.getContactEmail().isBlank()) {
                    body.append(" – Kontakt: ").append(s.getContactEmail());
                }
                body.append("\n");
            }

            body.append("\nLogga in på adminpanelen för att hantera sponsorerna.\n\n");
            body.append("Med vänliga hälsningar,\nFBK Balkan System");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("FBK Balkan – Sponsoravtal löper ut snart");
            message.setText(body.toString());
            mailSender.send(message);
            log.info("Sponsor expiry notification sent for {} sponsors expiring within {} days", expiringSponsors.size(), daysUntilExpiry);
        } catch (Exception e) {
            log.error("Failed to send sponsor expiry notification: {}", e.getMessage());
        }
    }
}