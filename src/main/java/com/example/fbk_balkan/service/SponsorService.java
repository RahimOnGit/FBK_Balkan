package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.SponsorDTO;
import com.example.fbk_balkan.entity.Sponsor;
import com.example.fbk_balkan.enums.SponsorCategory;
import com.example.fbk_balkan.repository.SponsorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SponsorService {

    @Autowired
    private SponsorRepository sponsorRepository;

    public List<Sponsor> getAllSponsors() {
        return sponsorRepository.findAllByOrderByCategoryAscNameAsc();
    }

    public List<Sponsor> getActiveSponsors() {
        return sponsorRepository.findByActiveTrueOrderByCategoryAscNameAsc();
    }

    public Optional<Sponsor> getSponsorById(Long id) {
        return sponsorRepository.findById(id);
    }

    public Map<SponsorCategory, List<Sponsor>> getActiveSponsorsByCategory() {
        Map<SponsorCategory, List<Sponsor>> result = new LinkedHashMap<>();
        for (SponsorCategory cat : SponsorCategory.values()) {
            List<Sponsor> sponsors = sponsorRepository.findByCategoryAndActiveTrueOrderByNameAsc(cat);
            if (!sponsors.isEmpty()) {
                result.put(cat, sponsors);
            }
        }
        return result;
    }

    public List<Sponsor> findExpiringSoon(int days) {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(days);
        return sponsorRepository.findExpiringSoon(from, to);
    }

    @Transactional
    public Sponsor createSponsor(SponsorDTO dto) {
        Sponsor sponsor = new Sponsor();
        mapDtoToEntity(dto, sponsor);
        return sponsorRepository.save(sponsor);
    }

    @Transactional
    public Sponsor updateSponsor(Long id, SponsorDTO dto) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sponsor hittades inte"));
        mapDtoToEntity(dto, sponsor);
        return sponsorRepository.save(sponsor);
    }

    @Transactional
    public void deleteSponsor(Long id) {
        sponsorRepository.deleteById(id);
    }

    @Transactional
    public void toggleActive(Long id) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sponsor hittades inte"));
        sponsor.setActive(!sponsor.isActive());
        sponsorRepository.save(sponsor);
    }

    private void mapDtoToEntity(SponsorDTO dto, Sponsor sponsor) {
        sponsor.setName(dto.getName());
        sponsor.setLogoUrl(dto.getLogoUrl());
        sponsor.setWebsiteUrl(dto.getWebsiteUrl());
        sponsor.setDescription(dto.getDescription());
        sponsor.setCategory(dto.getCategory());
        sponsor.setContactName(dto.getContactName());
        sponsor.setContactEmail(dto.getContactEmail());
        sponsor.setContactPhone(dto.getContactPhone());
        sponsor.setAgreementStart(dto.getAgreementStart());
        sponsor.setAgreementEnd(dto.getAgreementEnd());
        sponsor.setAmountSek(dto.getAmountSek());
        sponsor.setActive(dto.isActive());
    }
}
