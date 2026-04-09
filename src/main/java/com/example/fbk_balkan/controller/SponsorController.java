package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.SponsorDTO;
import com.example.fbk_balkan.entity.Sponsor;
import com.example.fbk_balkan.enums.SponsorCategory;
import com.example.fbk_balkan.service.FileStorageService;
import com.example.fbk_balkan.service.SponsorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class SponsorController {

    @Autowired
    private SponsorService sponsorService;

    @Autowired
    private FileStorageService fileStorageService;

    // =====================
    // PUBLIC
    // =====================

    @GetMapping("/sponsors")
    public String publicSponsorsPage(Model model) {
        Map<SponsorCategory, List<Sponsor>> byCategory = sponsorService.getActiveSponsorsByCategory();
        model.addAttribute("sponsorsByCategory", byCategory);
        model.addAttribute("categories", SponsorCategory.values());
        return "sponsors/public";
    }

    // =====================
    // ADMIN
    // =====================

    @GetMapping("/admin/sponsors")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminSponsorList(Model model) {
        List<Sponsor> sponsors = sponsorService.getAllSponsors();
        List<Sponsor> expiringSoon = sponsorService.findExpiringSoon(30);
        model.addAttribute("sponsors", sponsors);
        model.addAttribute("expiringSoon", expiringSoon);
        model.addAttribute("categories", SponsorCategory.values());
        return "admin/sponsors/list";
    }

    @GetMapping("/admin/sponsors/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("sponsorDTO", new SponsorDTO());
        model.addAttribute("categories", SponsorCategory.values());
        model.addAttribute("isEdit", false);
        return "admin/sponsors/form";
    }

    @PostMapping("/admin/sponsors/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createSponsor(
            @Valid @ModelAttribute SponsorDTO sponsorDTO,
            BindingResult result,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categories", SponsorCategory.values());
            model.addAttribute("isEdit", false);
            return "admin/sponsors/form";
        }

        if (logoFile != null && !logoFile.isEmpty()) {
            String logoUrl = fileStorageService.storeFile(logoFile);
            sponsorDTO.setLogoUrl(logoUrl);
        }

        sponsorService.createSponsor(sponsorDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Sponsor skapad!");
        return "redirect:/admin/sponsors";
    }

    @GetMapping("/admin/sponsors/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Sponsor sponsor = sponsorService.getSponsorById(id)
                .orElseThrow(() -> new RuntimeException("Sponsor hittades inte"));
        SponsorDTO dto = buildSponsorDTO(sponsor);
        model.addAttribute("sponsorDTO", dto);
        model.addAttribute("categories", SponsorCategory.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("sponsorId", id);
        model.addAttribute("existingLogoUrl", sponsor.getLogoUrl());
        return "admin/sponsors/form";
    }

    @PostMapping("/admin/sponsors/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateSponsor(
            @PathVariable Long id,
            @Valid @ModelAttribute SponsorDTO sponsorDTO,
            BindingResult result,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            @RequestParam(value = "deleteLogo", required = false, defaultValue = "false") String deleteLogoFlag,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categories", SponsorCategory.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("sponsorId", id);
            return "admin/sponsors/form";
        }

        Sponsor existing = sponsorService.getSponsorById(id)
                .orElseThrow(() -> new RuntimeException("Sponsor hittades inte"));

        if (logoFile != null && !logoFile.isEmpty()) {
            if (existing.getLogoUrl() != null) {
                fileStorageService.deleteFile(existing.getLogoUrl());
            }
            sponsorDTO.setLogoUrl(fileStorageService.storeFile(logoFile));
        } else if ("true".equals(deleteLogoFlag)) {
            if (existing.getLogoUrl() != null) {
                fileStorageService.deleteFile(existing.getLogoUrl());
            }
            sponsorDTO.setLogoUrl(null);
        } else {
            sponsorDTO.setLogoUrl(existing.getLogoUrl());
        }

        sponsorService.updateSponsor(id, sponsorDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Sponsor uppdaterad!");
        return "redirect:/admin/sponsors";
    }

    @PostMapping("/admin/sponsors/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteSponsor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Sponsor sponsor = sponsorService.getSponsorById(id)
                .orElseThrow(() -> new RuntimeException("Sponsor hittades inte"));
        if (sponsor.getLogoUrl() != null) {
            fileStorageService.deleteFile(sponsor.getLogoUrl());
        }
        sponsorService.deleteSponsor(id);
        redirectAttributes.addFlashAttribute("successMessage", "Sponsor borttagen!");
        return "redirect:/admin/sponsors";
    }

    @PostMapping("/admin/sponsors/toggle/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleActive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        sponsorService.toggleActive(id);
        redirectAttributes.addFlashAttribute("successMessage", "Sponsorstatus ändrad!");
        return "redirect:/admin/sponsors";
    }

    // =====================
    // HELPERS
    // =====================

    private SponsorDTO buildSponsorDTO(Sponsor sponsor) {
        SponsorDTO dto = new SponsorDTO();
        dto.setId(sponsor.getId());
        dto.setName(sponsor.getName());
        dto.setLogoUrl(sponsor.getLogoUrl());
        dto.setWebsiteUrl(sponsor.getWebsiteUrl());
        dto.setDescription(sponsor.getDescription());
        dto.setCategory(sponsor.getCategory());
        dto.setContactName(sponsor.getContactName());
        dto.setContactEmail(sponsor.getContactEmail());
        dto.setContactPhone(sponsor.getContactPhone());
        dto.setAgreementStart(sponsor.getAgreementStart());
        dto.setAgreementEnd(sponsor.getAgreementEnd());
        dto.setAmountSek(sponsor.getAmountSek());
        dto.setActive(sponsor.isActive());
        return dto;
    }
}
