package com.example.fbk_balkan.service;

import com.example.fbk_balkan.entity.TrialRegistration;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TrialEmailService {

    private static final Logger log = LoggerFactory.getLogger(TrialEmailService.class);

    private final Resend resend;

    @Value("${mail.from}")
    private String fromEmail;

    @Value("${mail.from-name:FBK Balkan Juniorklubb}")
    private String fromName;

    public TrialEmailService(Resend resend) {
        this.resend = resend;
    }

    public boolean sendApprovalEmail(TrialRegistration reg) {
        String subject = "Provträning godkänd – Välkommen till FBK Balkan! ⚽";

        String body = """
                Hej %s!

                Vi är glada att meddela att %s %s har blivit **godkänd** för provträning hos FBK Balkan.

                Vi ser fram emot att träffa er på planen!

                ────────────────────────────────
                VIKTIG PARKERINGSINFORMATION
                ────────────────────────────────
                • Röda markeringar = parkeringsplatser (aktivera parkeringsappen)
                • Gula markeringar = lämna/hämta-zon (får stå kvar i bilen)
                • Grön markering   = parkeringshus
                • Turkos markering = gångväg för barnen

                Tänk på att andra lag tränar efter oss – hjälp oss hålla flytet!

                Välkommen till laget!
                Med vänliga hälsningar,
                FBK Balkan Juniorklubb
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

                Vi har tyvärr inte möjlighet att erbjuda %s %s en plats just nu.

                Vi önskar er lycka till och hoppas att ni hittar ett bra lag.

                Med vänliga hälsningar,
                FBK Balkan Juniorklubb
                """.formatted(
                reg.getRelativeName(),
                reg.getFirstName(),
                reg.getLastName()
        );

        return sendEmail(reg.getRelativeEmail(), subject, body);
    }

    private boolean sendEmail(String to, String subject, String textBody) {
        try {
            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(fromName + " <" + fromEmail + ">")
                    .to(to)
                    .subject(subject)
                    .text(textBody)
                    .build();

            resend.emails().send(options);
            log.info("✅ Trial email sent successfully to: {}", to);
            return true;

        } catch (ResendException e) {
            log.error("❌ Failed to send trial email to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }
}