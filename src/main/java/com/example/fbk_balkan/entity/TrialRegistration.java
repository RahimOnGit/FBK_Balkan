package com.example.fbk_balkan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "trial_registrations")
public class TrialRegistration {

    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "child_id" , nullable = false )
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String relativeName;

    @Column(nullable = false)
    private String relativeEmail;

    @Column(nullable = false)
    private String relativeNumber;

//    trial info
   @Column(nullable = false)
   private LocalDate preferredTrainingDate;

    private TrialStatus status;

    @Column(name = "created_at")
    private LocalDate createdAt;
    @Column(nullable = true)  // optional field
    private String gender;  // Kön

    @Column(nullable = true)
    private String currentClub;  // Nuvarande klubb

    @Column(nullable = true)
    private Integer clubYears;

    @Column(name = "referral_source", length = 50)
    private String referralSource;

    @Column(name = "referral_other", length = 50)
    private String referralOther;


    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            this.createdAt = LocalDate.now();

        }
         }
}

