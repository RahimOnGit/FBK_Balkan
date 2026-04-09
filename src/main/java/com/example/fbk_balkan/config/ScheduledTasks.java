package com.example.fbk_balkan.config;

import com.example.fbk_balkan.entity.Sponsor;
import com.example.fbk_balkan.service.EmailNotificationService;
import com.example.fbk_balkan.service.SponsorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private SponsorService sponsorService;

    @Autowired
    private EmailNotificationService emailNotificationService;

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
}