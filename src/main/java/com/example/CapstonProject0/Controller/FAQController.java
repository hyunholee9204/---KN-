package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.Entity.FAQEntity;
import com.example.CapstonProject0.Service.FAQService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class FAQController {

    private final FAQService faqService;

    public FAQController(FAQService faqService) {
        this.faqService = faqService;
    }

    @GetMapping("/inquiry/faq")
    public String showFaqPage(Model model) {
        List<FAQEntity> faqs = faqService.getAllFaqs();
        model.addAttribute("faqs", faqs);
        return "Inquiry/faq";  // templates/faq.html
    }
}
