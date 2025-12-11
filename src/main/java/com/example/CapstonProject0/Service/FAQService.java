package com.example.CapstonProject0.Service;

import com.example.CapstonProject0.Entity.FAQEntity;
import com.example.CapstonProject0.Repository.FAQRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FAQService {

    private final FAQRepository faqRepository;

    public FAQService(FAQRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<FAQEntity> getAllFaqs() {
        return faqRepository.findAll();
    }
}
