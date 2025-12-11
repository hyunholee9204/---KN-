package com.example.CapstonProject0.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {





    @GetMapping("/education")
    public String educationPage() {
        return "EI/Education_Information";
    }

    @GetMapping("/inquiry")
    public String inquiryPage() {
        return "Inquiry/Inquiry";
    }

    // login, signup은 LoginController에서 처리하고 있으므로 생략
}
