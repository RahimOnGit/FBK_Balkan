package com.example.fbk_balkan.service;

import com.example.fbk_balkan.entity.TrialRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppNotificationService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotificationService.class);

    /**
     * Placeholder for integration with WhatsApp Business / Twilio / other provider.
     * Configure a real API client here to send messages to coach and parent.
     */
    public void sendTrialRegistrationConfirmation(TrialRegistration registration) {
        log.info("Trial registration received for child: {}, parent email: {}, phone: {}",
                registration.getChildName(),
                registration.getEmail(),
                registration.getPhoneNumber());
        // TODO: Implement real WhatsApp integration
    }
}




