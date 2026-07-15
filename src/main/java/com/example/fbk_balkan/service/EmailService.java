package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.ContactFormDTO;
import com.example.fbk_balkan.entity.TrialRegistration;

public interface EmailService {

    void sendRegistrationConfirmation(String to, String playerName, String token);

    void sendPasswordReset(String to, String resetLink, int validityMinutes);

    void sendNewsPublishedNotification(/* parameters */);

    void sendContactForm(/* parameters */);

    boolean sendTrialApproval(TrialRegistration reg);

    boolean sendTrialRejection(TrialRegistration reg);
    void sendContactForm(String adminEmail, ContactFormDTO dto);
    // Add more methods as needed
}