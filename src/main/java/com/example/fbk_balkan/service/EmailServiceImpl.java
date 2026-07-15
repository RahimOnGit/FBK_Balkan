package com.example.fbk_balkan.service;

import com.example.fbk_balkan.entity.TrialRegistration;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final Resend resend;
    private final TemplateEngine templateEngine;

    @Value("${mail.from}")
    private String fromEmail;

    @Value("${mail.from-name:FBK Balkan Juniorklubb}")
    private String fromName;

    public EmailServiceImpl(Resend resend, TemplateEngine templateEngine) {
        this.resend = resend;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendRegistrationConfirmation(String to, String playerName, String token) {
        Context context = new Context();
        context.setVariable("playerName", playerName);
        context.setVariable("confirmationLink", "https://fbkbalkan.se/confirm?token=" + token);

        String htmlContent = templateEngine.process("emails/registration-confirmation", context);

        sendEmail(to, "Välkommen till FBK Balkan!", htmlContent);
    }

    @Override
    public void sendPasswordReset(String to, String resetLink, int validityMinutes) {
        String subject = "Återställ ditt lösenord - FBK Balkan";
        String body = "Hej!\n\nKlicka här för att återställa lösenord (giltigt " + validityMinutes + " minuter):\n" + resetLink;
        sendSimpleEmail(to, subject, body);
    }

    @Override
    public boolean sendTrialApproval(TrialRegistration reg) {
        String subject = "Provträning godkänd – Välkommen till FBK Balkan! ⚽";

        String body = """
                Hej %s!

                Vi är glada att meddela att %s %s har blivit godkänd för provträning hos FBK Balkan.

                Vi ser fram emot att träffa er!

                Viktigt om parkering:
                • Röda = parkeringsplatser
                • Gula = lämna/hämta-zon
                • Grön = parkeringshus

                Välkommen till laget!
                FBK Balkan Juniorklubb
                """.formatted(reg.getRelativeName(), reg.getFirstName(), reg.getLastName());

        return sendSimpleEmail(reg.getRelativeEmail(), subject, body);
    }

    @Override
    public boolean sendTrialRejection(TrialRegistration reg) {
        String subject = "Angående din provträningsansökan – FBK Balkan";

        String body = """
                Hej %s!

                Tack för ert intresse för FBK Balkan.

                Vi har tyvärr inte möjlighet att erbjuda %s %s en plats just nu.

                Lycka till i er fortsatta fotbollssatsning!

                Med vänliga hälsningar,
                FBK Balkan Juniorklubb
                """.formatted(reg.getRelativeName(), reg.getFirstName(), reg.getLastName());

        return sendSimpleEmail(reg.getRelativeEmail(), subject, body);
    }

    // Stub methods for now - we can fill them later
    @Override
    public void sendNewsPublishedNotification() {
        log.warn("sendNewsPublishedNotification not implemented yet");
    }

    @Override
    public void sendContactForm() {
        log.warn("sendContactForm not implemented yet");
    }

    // ==================== Private helpers ====================

    private boolean sendSimpleEmail(String to, String subject, String text) {
        try {
            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(fromName + " <" + fromEmail + ">")
                    .to(to)
                    .subject(subject)
                    .text(text)
                    .build();

            CreateEmailResponse response = resend.emails().send(options);
            log.info("✅ Email sent to {} | ID: {}", to, response.getId());
            return true;
        } catch (ResendException e) {
            log.error("❌ Failed to send email to {}", to, e);
            return false;
        }
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(fromName + " <" + fromEmail + ">")
                    .to(to)
                    .subject(subject)
                    .html(htmlContent)
                    .build();

            resend.emails().send(options);
            log.info("✅ HTML Email sent to {}", to);
        } catch (ResendException e) {
            log.error("❌ Failed to send HTML email to {}", to, e);
        }
    }
}