package com.example.fbk_balkan.service;

import com.example.fbk_balkan.entity.TrialRegistration;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class TrialEmailService {

    private static final Logger log = LoggerFactory.getLogger(TrialEmailService.class);
    private final JavaMailSender mailSender;

    // Grabs the exact email used for SMTP auth from application.properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    public TrialEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendApprovalEmail(TrialRegistration reg) {
        String subject = "Provträning godkänd – FBK Balkan";

        String body = """
                Hej %s!

                Vi är glada att meddela att %s %s har blivit godkänd för provträning hos FBK Balkan.

                Vi ser fram emot att träffa er!

                ────────────────────────────────
                PARKERING – viktig information
                ────────────────────────────────
                Det är trångt på parkeringen. Vänligen hitta andra alternativ
                när ni hämtar och lämnar barn.

                • Röda markeringar  = parkeringsplatser (glöm inte att aktivera
                  parkeringsappen – det är gratis men kräver aktivering)
                • Gula markeringar  = lämna/hämta-zon (man FÅR stå kvar i bilen)
                • Grön markering    = parkeringshus för längre besök
                • Turkos markering  = gångväg för barnen från parkering till
                  hämtningsplatsen

                Tänk på att andra lag tränar efter oss – hjälp oss att komma in
                och ut smidigt!

                ────────────────────────────────
                Välkommen till laget!

                Med vänliga hälsningar,
                FBK Balkan
                """.formatted(
                reg.getRelativeName(),
                reg.getFirstName(),
                reg.getLastName()
        );

        return sendEmail(reg.getRelativeEmail(), subject, body);
    }

    public boolean sendRejectionEmail(TrialRegistration reg) {
        String subject = "Angående din provträningsansökan – FBK Balkan";

        String body = """
                Hej %s!

                Tack för att ni visade intresse för FBK Balkan.

                Vi har tyvärr inte möjlighet att erbjuda %s %s en plats hos oss just nu.

                Vi önskar er lycka till och hoppas att ni hittar ett bra lag.

                Med vänliga hälsningar,
                FBK Balkan
                """.formatted(
                reg.getRelativeName(),
                reg.getFirstName(),
                reg.getLastName()
        );

        return sendEmail(reg.getRelativeEmail(), subject, body);
    }

    private boolean sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            // This is the correct way to set a sender name + email address safely
            helper.setFrom(fromEmail, "FBK Balkan");

            mailSender.send(message);
            return true;

        } catch (MessagingException | MailException | UnsupportedEncodingException e) {
            // Using the proper logger prints the complete error stack trace to Render
            log.error("[TrialEmailService] Kunde inte skicka mejl till {}: ", to, e);
            return false;
        }
    }
}