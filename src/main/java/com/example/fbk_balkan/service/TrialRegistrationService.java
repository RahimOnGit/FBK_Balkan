package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.TrialRegistrationRequest;
import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.repository.TrialRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrialRegistrationService {

    private final TrialRegistrationRepository repository;
    private final WhatsAppNotificationService notificationService;

    public TrialRegistrationService(TrialRegistrationRepository repository,
                                    WhatsAppNotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @Transactional
    public TrialRegistration register(TrialRegistrationRequest request) {
        // Check if email already exists
        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("En registrering med denna e-postadress finns redan.");
        }

        TrialRegistration registration = new TrialRegistration();
        registration.setChildName(request.getChildName());
        registration.setBirthYear(request.getBirthYear());
        registration.setGender(request.getGender());
        registration.setCurrentClub(request.getCurrentClub());
        registration.setYearsInCurrentClub(request.getYearsInCurrentClub());
        registration.setEmail(request.getEmail());
        registration.setPhoneNumber(request.getPhoneNumber());
        registration.setTeamCategory(request.getTeamCategory());

        TrialRegistration saved = repository.save(registration);
        notificationService.sendTrialRegistrationConfirmation(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<TrialRegistration> findAll() {
        return repository.findAll();
    }
}




