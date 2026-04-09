package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.Sponsor;
import com.example.fbk_balkan.enums.SponsorCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Long> {

    List<Sponsor> findByCategoryAndActiveTrueOrderByNameAsc(SponsorCategory category);

    List<Sponsor> findByActiveTrueOrderByCategoryAscNameAsc();

    List<Sponsor> findAllByOrderByCategoryAscNameAsc();

    @Query("SELECT s FROM Sponsor s WHERE s.active = true AND s.agreementEnd IS NOT NULL AND s.agreementEnd BETWEEN :from AND :to")
    List<Sponsor> findExpiringSoon(@Param("from") LocalDate from, @Param("to") LocalDate to);
}