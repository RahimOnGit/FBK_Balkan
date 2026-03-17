package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.FaqFormDTO;
import com.example.fbk_balkan.entity.Faq;
import com.example.fbk_balkan.repository.FaqRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

public List<Faq> getAllFaqs() {
    return faqRepository.findAll(Sort.by("displayOrder"));
}
    public List<Faq> getVisibleFaqs() {
        return faqRepository.findByVisibleTrueOrderByDisplayOrderAsc();
    }

    public Faq getById(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found"));
    }
    @Transactional
    public void save(FaqFormDTO dto) {

        Faq faq;

        if (dto.getId() != null) {
            faq = getById(dto.getId());
        } else {
            faq = new Faq();
        }

        faq.setQuestion(dto.getQuestion());
        faq.setAnswer(dto.getAnswer());
        faq.setDisplayOrder(dto.getDisplayOrder());
        faq.setVisible(dto.getVisible());

        faqRepository.save(faq);
    }

    public void delete(Long id) {
        faqRepository.deleteById(id);
    }
}
