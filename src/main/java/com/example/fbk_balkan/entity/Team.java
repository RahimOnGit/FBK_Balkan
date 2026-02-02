package com.example.fbk_balkan.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "teams") //
@EntityListeners(AuditingEntityListener.class) // Enables auto-timestamps
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // e.g., "FBK Balkan P08 Pojkar"

    @Column(nullable = false, length = 20)
    private String ageGroup; // Standardized: "P08", "F10", "U15" (Swedish convention)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender; // CRITICAL: MALE, FEMALE, MIXED

    @Column(name = "training_location", length = 150) // ✅ Specific ground name
    private String trainingLocation; // e.g., "kriseberg IP"

    @ManyToOne
    @JoinColumn(name = "coach_id") // Foreign key to Coach table
    private Coach coach;

    //  Add @Transient getCoachId() later for API
    //  NEVER store contact details here GDPR!

    @Column(nullable = false)
    private boolean active = true; // Default active

    @Column(columnDefinition = "TEXT")
    private String description; // till exampl "Tränar  18:00, nybörjare välkomna!"

    //  AUDITING FIELDS
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    // INNER ENUM
    public enum Gender {
        MALE,    // "Pojkar" in Swedish UI
        FEMALE,  // "Flickor"
        MIXED    // "Mixad"
    }
}