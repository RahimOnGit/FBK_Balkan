package com.example.fbk_balkan.config;

import com.example.fbk_balkan.entity.Sponsor;
import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.repository.TrialRegistrationRepository;
import com.example.fbk_balkan.service.EmailNotificationService;
import com.example.fbk_balkan.service.SponsorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;

@Configuration
@EnableScheduling
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final int TRIAL_RETENTION_DAYS = 20;

    @Autowired
    private SponsorService sponsorService;

    @Autowired
    private EmailNotificationService emailNotificationService;

    @Autowired
    private TrialRegistrationRepository trialRegistrationRepository;

    @Scheduled(cron = "0 0 8 * * MON")
    public void checkExpiringSponsorAgreements() {
        log.info("Running sponsor expiry check...");

        List<Sponsor> expiring30 = sponsorService.findExpiringSoon(30);
        if (!expiring30.isEmpty()) {
            emailNotificationService.sendSponsorExpiryNotification(expiring30, 30);
        }

        List<Sponsor> expiring14 = sponsorService.findExpiringSoon(14);
        if (!expiring14.isEmpty()) {
            emailNotificationService.sendSponsorExpiryNotification(expiring14, 14);
        }

        List<Sponsor> expiring7 = sponsorService.findExpiringSoon(7);
        if (!expiring7.isEmpty()) {
            emailNotificationService.sendSponsorExpiryNotification(expiring7, 7);
        }
    }

    /**
     * GDPR data retention: deletes trial registration records whose trial date
     * ended more than {@value TRIAL_RETENTION_DAYS} days ago.
     * Runs every day at midnight.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredTrialRegistrations() {
        LocalDate cutoff = LocalDate.now().minusDays(TRIAL_RETENTION_DAYS);
        List<TrialRegistration> expired = trialRegistrationRepository.findByPreferredTrainingDateBefore(cutoff);

        if (expired.isEmpty()) {
            log.info("[GDPR] No expired trial registrations to delete.");
            return;
        }

        trialRegistrationRepository.deleteAll(expired);
        log.info("[GDPR] Deleted {} trial registration(s) with a training date before {} ({} day retention policy).",
                expired.size(), cutoff, TRIAL_RETENTION_DAYS);
    }
}