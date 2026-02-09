package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
import com.example.fbk_balkan.enums.ReferralSource;
import com.example.fbk_balkan.repository.TrialRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TrialRegistrationService {

    @Autowired
    private  TrialRegistrationRepository trialRegistrationRepository;

    //    create method
    public TrialRegistrationDTO create(TrialRegistrationDTO trialRegistrationDTO) {
        // Sanitize all string fields
        String firstName = sanitize(trialRegistrationDTO.getFirstName());
        String lastName = sanitize(trialRegistrationDTO.getLastName());
        String relativeName = sanitize(trialRegistrationDTO.getRelativeName());
        String relativeEmail = sanitize(trialRegistrationDTO.getRelativeEmail());
        String relativeNumber = sanitize(trialRegistrationDTO.getRelativeNumber());
        String currentClub = sanitize(trialRegistrationDTO.getCurrentClub());
        String referralOther = sanitize(trialRegistrationDTO.getReferralOther());
        // Map DTO to entity
        var trialRegistration = new com.example.fbk_balkan.entity.TrialRegistration();
        trialRegistration.setFirstName(trialRegistrationDTO.getFirstName());
        trialRegistration.setLastName(trialRegistrationDTO.getLastName());
        trialRegistration.setBirthDate(trialRegistrationDTO.getBirthDate());
        trialRegistration.setRelativeName(trialRegistrationDTO.getRelativeName());
        trialRegistration.setRelativeEmail(trialRegistrationDTO.getRelativeEmail());
        trialRegistration.setRelativeNumber(trialRegistrationDTO.getRelativeNumber());
        trialRegistration.setPreferredTrainingDate(trialRegistrationDTO.getPreferredTrainingDate());
        trialRegistration.setGender(trialRegistrationDTO.getGender());          // NEW
        trialRegistration.setCurrentClub(trialRegistrationDTO.getCurrentClub()); // NEW
        trialRegistration.setClubYears(trialRegistrationDTO.getClubYears());     // NEW
        trialRegistration.setReferralSource(trialRegistrationDTO.getReferralSource());// NEW

        trialRegistration.setReferralOther(
                trialRegistrationDTO.getReferralSource() == ReferralSource.OTHER
                        ? trialRegistrationDTO.getReferralOther()
                        : null
        );

        trialRegistration.setStatus(com.example.fbk_balkan.entity.TrialStatus.PENDING);
        trialRegistration.setCreatedAt(LocalDate.now());

        // Save entity
        trialRegistrationRepository.save(trialRegistration);

        // used here  builder methode to add new fields any time easily.
        return TrialRegistrationDTO.builder()
                .id(trialRegistration.getId())
                .firstName(trialRegistration.getFirstName())
                .lastName(trialRegistration.getLastName())
                .birthDate(trialRegistration.getBirthDate())
                .relativeName(trialRegistration.getRelativeName())
                .relativeEmail(trialRegistration.getRelativeEmail())
                .relativeNumber(trialRegistration.getRelativeNumber())
                .preferredTrainingDate(trialRegistration.getPreferredTrainingDate())
                .gender(trialRegistration.getGender())          // NEW
                .currentClub(trialRegistration.getCurrentClub()) // NEW
                .clubYears(trialRegistration.getClubYears())    // NEW
                .referralSource(trialRegistration.getReferralSource())  // NEW
                .referralOther(trialRegistration.getReferralOther())// NEW
                .status(trialRegistration.getStatus())
                .createdAt(trialRegistration.getCreatedAt())
                .build();

    }


// Sanitization helper
private String sanitize(String value) {
    if (value == null) return null;
    return value.trim().replaceAll("<.*?>", ""); // remove HTML tags
}}