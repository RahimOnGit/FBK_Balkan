package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.entity.TrialStatus;
import com.example.fbk_balkan.enums.Gender;
import com.example.fbk_balkan.enums.ReferralSource;
import com.example.fbk_balkan.repository.TrialRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrialRegistrationService {

    private final TrialRegistrationRepository trialRegistrationRepository;

    @Autowired
    public TrialRegistrationService(TrialRegistrationRepository trialRegistrationRepository) {
        this.trialRegistrationRepository = trialRegistrationRepository;
    }

    @Transactional
    public TrialRegistrationDTO create(TrialRegistrationDTO dto) {
        System.out.println("===== Starta sparprocessen i Service =====");

        // Sanitize
        String firstName = sanitize(dto.getFirstName());
        String lastName = sanitize(dto.getLastName());
        String relativeName = sanitize(dto.getRelativeName());
        String relativeEmail = sanitize(dto.getRelativeEmail());
        String relativeNumber = sanitize(dto.getRelativeNumber());
        String currentClub = sanitize(dto.getCurrentClub());
        String referralOther = sanitize(dto.getReferralOther());

        // Skapande av entitet
        TrialRegistration entity = new TrialRegistration();
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setBirthDate(dto.getBirthDate());
        entity.setRelativeName(relativeName);
        entity.setRelativeEmail(relativeEmail);
        entity.setRelativeNumber(relativeNumber);
        entity.setPreferredTrainingDate(dto.getPreferredTrainingDate());
        entity.setGender(dto.getGender());
        entity.setCurrentClub(currentClub);
        entity.setClubYears(dto.getClubYears());
        entity.setReferralSource(dto.getReferralSource());
        entity.setReferralOther(
                dto.getReferralSource() == ReferralSource.OTHER ? referralOther : null
        );
        entity.setStatus(TrialStatus.PENDING);
        entity.setCreatedAt(LocalDate.now());

        // Viktig anmärkning: Om du vill tilldela en coach automatiskt (t.ex. den första coachen eller enligt en specifik logik)
        // entity.setCoach(...); ← Lägg till här om det behövs
        // För närvarande lämnar vi det null eftersom nullable=true

        try {
            TrialRegistration saved = trialRegistrationRepository.save(entity);
            System.out.println("Sparat framgångsrikt → Nytt ID: " + saved.getId());

            return TrialRegistrationDTO.builder()
                    .id(saved.getId())
                    .firstName(saved.getFirstName())
                    .lastName(saved.getLastName())
                    .birthDate(saved.getBirthDate())
                    .relativeName(saved.getRelativeName())
                    .relativeEmail(saved.getRelativeEmail())
                    .relativeNumber(saved.getRelativeNumber())
                    .preferredTrainingDate(saved.getPreferredTrainingDate())
                    .gender(saved.getGender())
                    .currentClub(saved.getCurrentClub())
                    .clubYears(saved.getClubYears())
                    .referralSource(saved.getReferralSource())
                    .referralOther(saved.getReferralOther())
                    .status(saved.getStatus())
                    .createdAt(saved.getCreatedAt() != null ? saved.getCreatedAt().atStartOfDay() : null)
                    .build();
        } catch (Exception e) {
            System.err.println("Fel när Provträningsregistrering skulle sparas: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Misslyckades med att spara inspelningen: " + e.getMessage(), e);
        }
    }

    // Hämtningsfunktion av tränare (omodifierad)
    public List<TrialRegistrationDTO> fetchTrialRegistrationByCoach(Long coachId) {
        return trialRegistrationRepository.findByCoachIdOrderByCreatedAtDesc(coachId)
                .stream()
                .map(TrialRegistrationDTO::fromEntity)
                .toList();
    }

    private String sanitize(String value) {
        if (value == null) return null;
        return value.trim().replaceAll("<.*?>", "");
    }
}