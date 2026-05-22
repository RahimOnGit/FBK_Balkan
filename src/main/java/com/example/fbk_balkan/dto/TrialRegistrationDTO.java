package com.example.fbk_balkan.dto;

import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.entity.TrialStatus;
import com.example.fbk_balkan.enums.Gender;
import com.example.fbk_balkan.enums.ReferralSource;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrialRegistrationDTO {

    private Long id;

    @NotBlank(message = "Förnamn är obligatoriskt")
    @Size(min = 2, max = 40, message = "Måste vara mellan 2 och 40 tecken")
    @Pattern(
            regexp = "^[\\p{L} .'-]+$",
            message = "Endast bokstäver och giltiga namntecken tillåtna")
    private String firstName;

    @NotBlank(message = "Efternamn är obligatoriskt")
    @Size(min = 2, max = 40, message = "Måste vara mellan 2 och 40 tecken")
    @Pattern(
            regexp = "^[\\p{L} .'-]+$",
            message = "Endast bokstäver och giltiga namntecken tillåtna")
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Födelsedatum är obligatoriskt")
    @Past(message = "Födelsedatum måste vara i det förflutna")
    private LocalDate birthDate;

    @NotBlank(message = "Anhörigs namn är obligatoriskt")
    @Size(min = 2, max = 40, message = "Måste vara mellan 2 och 40 tecken")
    @Pattern(
            regexp = "^[\\p{L} .'-]+$",
            message = "Endast bokstäver och giltiga namntecken tillåtna")
    private String relativeName;

    @NotBlank(message = "Anhörigs e-postadress är obligatorisk")
    @Email(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$",
            message = "Ogiltig e-postadress")
    @Size(max = 40, message = "E-postadressen får inte överstiga 40 tecken")
    private String relativeEmail;

    @NotBlank(message = "Anhörigs telefonnummer är obligatoriskt")
    @Pattern(
            regexp = "^\\+?[0-9 ]{8,15}$",
            message = "Ogiltigt telefonnummer")
    private String relativeNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Önskat träningsdatum är obligatoriskt")
    private LocalDate preferredTrainingDate;

    private TrialStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @NotNull(message = "Kön är obligatoriskt")
    private Gender gender;

    @Size(max = 40, message = "Max 40 tecken")
    @Pattern(
            regexp = "^[\\p{L}0-9 .'-]*$",
            message = "Ogiltiga tecken")
    private String currentClub;

    @Min(value = 0, message = "Antal år i klubb kan inte vara negativt")
    @Max(value = 5, message = "Antal år i klubb kan inte vara mer än 5")
    private Integer clubYears;

    private ReferralSource referralSource;

    @Size(max = 50, message = "Max 50 tecken")
    @Pattern(
            regexp = "^[\\p{L}0-9 .,'\"!?-]*$",
            message = "Ogiltiga tecken")
    private String referralOther;

    // Tränaren som tilldelats denna registrering (bestäms från födelseår och kön)
    // Null innebär att inget matchande lag hittades — admin måste tilldela manuellt
    private Long coachId;

    // =========================================================
    // fromEntity — används vid hämtning av registreringar från databasen
    //              (t.ex. för coach dashboard listan)
    // =========================================================
    public static TrialRegistrationDTO fromEntity(TrialRegistration entity) {
        if (entity == null) return null;
        return TrialRegistrationDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .birthDate(entity.getBirthDate())
                .relativeName(entity.getRelativeName())
                .relativeEmail(entity.getRelativeEmail())
                .relativeNumber(entity.getRelativeNumber())
                .preferredTrainingDate(entity.getPreferredTrainingDate())
                .status(entity.getStatus())
//                .createdAt(entity.getCreatedAt().atStartOfDay())
                .createdAt(entity.getCreatedAt())
                .gender(entity.getGender())
                .currentClub(entity.getCurrentClub())
                .clubYears(entity.getClubYears())
                .referralSource(entity.getReferralSource())
                .referralOther(entity.getReferralOther())
                // Sätt coachId så att dashboarden vet vilken tränare denna förfrågan tillhör
                .coachId(entity.getCoach() != null ? entity.getCoach().getId() : null)
                .build();
    }

    public TrialRegistration toEntity() {
        TrialRegistration e = new TrialRegistration();
        e.setId(this.id);
        e.setFirstName(this.firstName);
        e.setLastName(this.lastName);
        e.setBirthDate(this.birthDate);
        e.setRelativeName(this.relativeName);
        e.setRelativeEmail(this.relativeEmail);
        e.setRelativeNumber(this.relativeNumber);
        e.setPreferredTrainingDate(this.preferredTrainingDate);
        e.setStatus(this.status);
//        e.setCreatedAt(LocalDate.from(this.createdAt));
        e.setCreatedAt(this.createdAt);
        e.setGender(this.gender);
        e.setCurrentClub(this.currentClub);
        e.setClubYears(this.clubYears);
        e.setReferralSource(this.referralSource);
        e.setReferralOther(
                this.referralSource == ReferralSource.OTHER ? this.referralOther : null
        );
        return e;
    }
}