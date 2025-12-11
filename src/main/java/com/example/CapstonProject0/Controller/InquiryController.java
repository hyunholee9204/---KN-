package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.DTO.InquiryForm;
import com.example.CapstonProject0.Entity.InquiryEntity;
import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Service.InquiryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("inquiryForm", new InquiryForm());
        return "Inquiry/Inquiry-register";
    }

    @PostMapping("/form")
    public String submitInquiry(@ModelAttribute InquiryForm form,
                                HttpSession session) throws IOException {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        inquiryService.saveInquiry(form, user.getId());
        return "redirect:/inquiry/my";
    }

    @GetMapping("/my")
    public String showMyInquiries(Model model, HttpSession session) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        List<InquiryEntity> list = inquiryService.findByUserId(user.getId());
        model.addAttribute("inquiries", list); // ← 이름 정확히 확인!
        return "Inquiry/inquiry-my";
    }

    @GetMapping("/guide")
    public String guide() {
        return "Inquiry/inquiry-guide"; // templates/inquiry/guide.html
    }
}
