package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.entity.TrialStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrialRegistrationRepository extends JpaRepository<TrialRegistration, Long> {


    long countByStatus(TrialStatus status);

    List<TrialRegistration> findTop10ByOrderByCreatedAtDesc();

    List<TrialRegistration> findByStatusOrderByCreatedAtDesc(TrialStatus status);

    //   trials reqs for coach
    List<TrialRegistration> findByCoachId(Long coachId);
    List<TrialRegistration> findByCoachIdAndStatus(Long coachId , TrialStatus status);
    List<TrialRegistration> findByCoachIdOrderByCreatedAtDesc(Long coachId);

    /**
     * Returns all PENDING registrations for a coach (any age),
     * plus APPROVED/REJECTED registrations created within the last 3 months.
     * Sorted newest first.
     */
    @Query("SELECT t FROM TrialRegistration t WHERE t.coach.id = :coachId " +
            "AND (t.status = com.example.fbk_balkan.entity.TrialStatus.PENDING " +
            "     OR (t.status <> com.example.fbk_balkan.entity.TrialStatus.PENDING AND t.createdAt >= :cutoff)) " +
            "ORDER BY t.createdAt DESC")
    List<TrialRegistration> findActiveAndRecentByCoachId(@Param("coachId") Long coachId,
                                                         @Param("cutoff") LocalDateTime cutoff);

    /**
     * Find all trial registrations where the child's birth date falls within a
     * given calendar year (used to bulk-reassign when a team coach is set).
     */
    @Query("SELECT t FROM TrialRegistration t WHERE t.birthDate >= :startDate AND t.birthDate < :endDate ORDER BY t.createdAt DESC")
    List<TrialRegistration> findByBirthDateYear(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDateAndPreferredTrainingDate(
            String firstName,
            String lastName,
            LocalDate birthDate,
            LocalDate preferredTrainingDate
    );
    boolean existsByRelativeEmailIgnoreCaseAndFirstNameIgnoreCase(
            String relativeEmail,
            String firstName
    );


}
