package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.enums.Gender;
import com.example.fbk_balkan.enums.ReferralSource;
import com.example.fbk_balkan.repository.TeamRepository;
import com.example.fbk_balkan.repository.TrialRegistrationRepository;
import com.example.fbk_balkan.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrialRegistrationTestDuplicate  {

    @Mock
    private TrialRegistrationRepository trialRegistrationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TrialRegistrationService trialRegistrationService;

    // =========================
    // SUCCESS CASE
    // =========================
    @Test
    void should_create_registration_when_valid() {

        TrialRegistrationDTO dto = TrialRegistrationDTO.builder()
                .firstName("Ali")
                .lastName("Ahmed")
                .relativeName("Parent Name")
                .relativeEmail("parent@mail.com")
                .relativeNumber("0701234567")
                .birthDate(LocalDate.of(2015, 6, 10))
                .preferredTrainingDate(LocalDate.now().plusDays(10))
                .gender(Gender.MALE)
                .referralSource(ReferralSource.OTHER)
                .build();

        when(trialRegistrationRepository
                .existsByRelativeEmailIgnoreCaseAndFirstNameIgnoreCase(any(), any()))
                .thenReturn(false);

        when(trialRegistrationRepository
                .existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDateAndPreferredTrainingDate(
                        any(), any(), any(), any()))
                .thenReturn(false);

        when(trialRegistrationRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        var result = trialRegistrationService.create(dto);

        assertNotNull(result);
        verify(trialRegistrationRepository, times(1)).save(any());
    }

    // =========================
    // DUPLICATE EMAIL + FIRST NAME
    // =========================
    @Test
    void should_throw_exception_when_email_and_firstname_duplicate() {

        TrialRegistrationDTO dto = TrialRegistrationDTO.builder()
                .firstName("Ali")
                .relativeEmail("parent@mail.com")
                .birthDate(LocalDate.of(2015, 6, 10))
                .preferredTrainingDate(LocalDate.now().plusDays(10))
                .build();

        when(trialRegistrationRepository
                .existsByRelativeEmailIgnoreCaseAndFirstNameIgnoreCase(any(), any()))
                .thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> trialRegistrationService.create(dto)
        );

        assertEquals(
                "Ett barn med detta förnamn är redan registrerat för denna e-postadress.",
                ex.getMessage()
        );
    }

    // =========================
    // DUPLICATE TRAINING SESSION
    // =========================
    @Test
    void should_throw_exception_when_same_training_registration_exists() {

        TrialRegistrationDTO dto = TrialRegistrationDTO.builder()
                .firstName("Ali")
                .lastName("Ahmed")
                .relativeEmail("parent@mail.com")
                .birthDate(LocalDate.of(2015, 6, 10))
                .preferredTrainingDate(LocalDate.now().plusDays(10))
                .build();

        // email check must be FALSE so second rule is reached
        when(trialRegistrationRepository
                .existsByRelativeEmailIgnoreCaseAndFirstNameIgnoreCase(any(), any()))
                .thenReturn(false);

        // training duplicate = TRUE
        when(trialRegistrationRepository
                .existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDateAndPreferredTrainingDate(
                        any(), any(), any(), any()))
                .thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> trialRegistrationService.create(dto)
        );

        assertEquals(
                "Barnet är redan registrerat för detta provträningstillfälle.",
                ex.getMessage()
        );
    }
}