package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Entity.TransactionEntity;
import com.example.CapstonProject0.Service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/asset/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("")
    public String showTransactionHistory(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        LoginEntity loginUser = (LoginEntity) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        int size = 15; // ✅ 한 페이지에 15개씩
        Page<TransactionEntity> transactionPage = transactionService.getTransactionsByUser(loginUser, page, size);

        model.addAttribute("transactions", transactionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionPage.getTotalPages());

        return "Asset/asset-transaction";
    }



}

