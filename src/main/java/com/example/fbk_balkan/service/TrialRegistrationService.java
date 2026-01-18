package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
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
        // Map DTO to entity
        var trialRegistration = new com.example.fbk_balkan.entity.TrialRegistration();
        trialRegistration.setFirstName(trialRegistrationDTO.getFirstName());
        trialRegistration.setLastName(trialRegistrationDTO.getLastName());
        trialRegistration.setBirthDate(trialRegistrationDTO.getBirthDate());
        trialRegistration.setRelativeName(trialRegistrationDTO.getRelativeName());
        trialRegistration.setRelativeEmail(trialRegistrationDTO.getRelativeEmail());
        trialRegistration.setRelativeNumber(trialRegistrationDTO.getRelativeNumber());
        trialRegistration.setPreferredTrainingDate(trialRegistrationDTO.getPreferredTrainingDate());
        trialRegistration.setStatus(com.example.fbk_balkan.entity.TrialStatus.PENDING);
        trialRegistration.setCreatedAt(LocalDate.now());

        // Save entity
        trialRegistrationRepository.save(trialRegistration);

        // Map entity back to DTO
        return new TrialRegistrationDTO(
                trialRegistration.getId(),
                trialRegistration.getFirstName(),
                trialRegistration.getLastName(),
                trialRegistration.getBirthDate(),
                trialRegistration.getRelativeName(),
                trialRegistration.getRelativeEmail(),
                trialRegistration.getRelativeNumber(),
                trialRegistration.getPreferredTrainingDate(),
                trialRegistration.getStatus(),
                trialRegistration.getCreatedAt()
        );
    }



}
