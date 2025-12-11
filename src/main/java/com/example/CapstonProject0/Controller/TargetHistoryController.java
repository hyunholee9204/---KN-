package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.DTO.TargetHistoryForm;
import com.example.CapstonProject0.Service.TargetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/target/history")
public class TargetHistoryController {

    private final TargetService targetService;

    // ✅ 저축/출금 내역 추가
    @PostMapping("/add")
    public String addHistory(@ModelAttribute TargetHistoryForm form) {
        targetService.addHistory(form);
        return "redirect:/target/progress";
    }
}
