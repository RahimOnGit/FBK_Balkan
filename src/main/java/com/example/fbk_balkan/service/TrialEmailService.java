package com.example.fbk_balkan.service;

import com.example.fbk_balkan.entity.TrialRegistration;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class TrialEmailService {

    private final JavaMailSender mailSender;

    public TrialEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

//      Skickar godkännandemejl till förälderns e-postadress.
//     Returnerar true om det lyckades, false annars.
//     
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

//
//      Skickar avslagsmejl till förälderns e-postadress.
//      Returnerar true om det lyckades, false annars.
//
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

    // ── privat hjälpmetod

    private boolean sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false); // false = plain text, inte HTML
            helper.setFrom("FBK Balkan <" + getFromAddress() + ">");

            mailSender.send(message);
            return true;

        } catch (MessagingException | MailException e) {
            // Logga felet men kasta inte – status har redan sparats i DB
            System.err.println("[TrialEmailService] Kunde inte skicka mejl till " + to + ": " + e.getMessage());
            return false;
        }
    }


     // Hämtar avsändaradressen från spring.mail.username om möjligt,
     // annars används en fast fallback.

    private String getFromAddress() {
        // Spring injicerar inte properties hit, men mailSender är redan konfigurerad
        // med rätt avsändare via application.properties.
        // Returnera något rimligt – SMTP-servern sätter den faktiska From-adressen.
        return "ungdom@fbkbalkan.se";
    }
}