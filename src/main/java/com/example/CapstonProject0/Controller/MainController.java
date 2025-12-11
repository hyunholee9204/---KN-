package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Service.AssetService;
import com.example.CapstonProject0.DTO.AssetTotalForm;
import com.example.CapstonProject0.Service.LoanService;
import com.example.CapstonProject0.Service.TargetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    private final AssetService assetService;
    private final LoanService loanService;
    private final TargetService targetService;

    public MainController(AssetService assetService, LoanService loanService, TargetService targetService) {
        this.assetService = assetService;
        this.loanService = loanService;
        this.targetService = targetService;
    }

    @GetMapping("/main")
    public String mainPage(HttpSession session, Model model) {
        LoginEntity loginUser = (LoginEntity) session.getAttribute("loginUser");

        if (loginUser != null) {
            // ✅ 자산
            List<AssetTotalForm> groupedAssets = assetService.getGroupedAssetsByUser(loginUser.getId());
            long totalAmount = groupedAssets.stream()
                    .mapToLong(AssetTotalForm::getTotalAmount)
                    .sum();
            model.addAttribute("assetAmount", totalAmount);

            // ✅ 대출
            Long totalLoanAmount = loanService.getTotalLoanAmount(loginUser);
            model.addAttribute("loanAmount", totalLoanAmount != null ? totalLoanAmount : 0L);

            // ✅ 목표 리스트
            List<String> targetList = targetService.getFormattedTargetsByUser(loginUser);
            model.addAttribute("targetList", targetList);
        }

        return "mainPage";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }
}
