package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.DTO.AssetForm;
import com.example.CapstonProject0.DTO.AssetTotalForm;
import com.example.CapstonProject0.Entity.*;
import com.example.CapstonProject0.Service.AssetService;
import com.example.CapstonProject0.Service.TargetService;
import com.example.CapstonProject0.Service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/asset")
public class AssetController {

    private final AssetService assetService;
    private final TargetService targetService;
    private final TransactionService transactionService;

    public AssetController(AssetService assetService, TargetService targetService, TransactionService transactionService) {
        this.assetService = assetService;
        this.targetService = targetService;
        this.transactionService = transactionService;
    }

    @GetMapping("")
    public String redirectToAssetStatus() {
        return "redirect:/asset/status";
    }

    @GetMapping("/register")
    public String showAssetForm(Model model) {
        model.addAttribute("assetForm", new AssetForm());
        return "Asset/asset-register";
    }

    @PostMapping("/register")
    public String registerAsset(@ModelAttribute AssetForm assetForm, HttpSession session) {
        LoginEntity loginUser = (LoginEntity) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        assetService.registerAsset(loginUser.getId(), assetForm);
        return "redirect:/asset/status";
    }

    @GetMapping("/status")
    public String assetStatus(HttpSession session, Model model) {
        LoginEntity loginUser = (LoginEntity) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        // 🔹 개별 자산 목록 (상세 내역 테이블용)
        List<AssetEntity> assetList = assetService.findByUserId(loginUser.getId());
        // 금액 내림차순 정렬
        assetList.sort((a, b) -> Long.compare(b.getAmount(), a.getAmount()));

        // 🔹 자산 종류별 합산 (카드용)
        List<AssetTotalForm> groupedAssets = assetService.getGroupedAssetsByUser(loginUser.getId());
        // 합산 금액 내림차순 정렬
        groupedAssets.sort((a, b) -> Long.compare(b.getTotalAmount(), a.getTotalAmount()));

        Long totalAmount = groupedAssets.stream()
                .mapToLong(AssetTotalForm::getTotalAmount)
                .sum();

        // 🔹 Map으로 변환 (카드 표시용)
        Map<String, Long> groupedAssetsMap = new HashMap<>();
        groupedAssets.forEach(asset -> groupedAssetsMap.put(asset.getType(), asset.getTotalAmount()));

        // 🔹 모델 데이터 세팅
        model.addAttribute("userName", loginUser.getName());
        model.addAttribute("assetList", assetList);           // 정렬된 개별 자산
        model.addAttribute("groupedAssets", groupedAssets);   // 정렬된 합산 데이터
        model.addAttribute("groupedAssetsMap", groupedAssetsMap);
        model.addAttribute("assetAmount", totalAmount);


        return "Asset/asset-status";
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        AssetEntity asset = assetService.findById(id);
        model.addAttribute("asset", asset);
        return "Asset/asset-update";
    }

    @PostMapping("/update/{id}")
    public String editAsset(@PathVariable("id") Long id,
                            @RequestParam("type") String type,
                            @RequestParam("amount") int amount) {
        assetService.updateAsset(id, type, amount);
        return "redirect:/asset/status";
    }

    @PostMapping("/deleteOne/{id}")
    public String deleteAsset(@PathVariable("id") Long id) {
        assetService.deleteById(id);
        return "redirect:/asset/status";
    }

    @GetMapping("/history")
    public String showMonthlyAsset(Model model, HttpSession session) {
        // ✅ 로그인 세션 확인
        LoginEntity loginUser = (LoginEntity) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        // ✅ 사용자별 월별 자산 변동 내역 조회
        List<AssetHistoryEntity> historyList = assetService.getMonthlyHistory(loginUser);

        // ✅ 그래프용 데이터 분리 (Thymeleaf에서 stream 사용 불가 → Controller에서 가공)
        List<String> months = historyList.stream()
                .map(h -> h.getRecordMonth().toString())  // 예: "2025-10"
                .toList();

        List<Long> totals = historyList.stream()
                .map(AssetHistoryEntity::getTotalAmount)
                .toList();

        // ✅ 뷰에 전달
        model.addAttribute("userName", loginUser.getName());
        model.addAttribute("monthlyHistory", historyList);
        model.addAttribute("months", months);
        model.addAttribute("totals", totals);

        return "Asset/Asset-history";
    }


    /** 🔹 목표 달성용 자산 등록 페이지 */
    @GetMapping("/goal")
    public String showGoalAssetForm(HttpSession session, Model model) {
        LoginEntity loginUser = (LoginEntity) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        List<TargetEntity> targets = targetService.getTargetsByUser(loginUser);
        model.addAttribute("targets", targets);
        model.addAttribute("assetForm", new AssetForm());

        return "Asset/Asset-goal";
    }

    /** 🔹 목표 달성용 자산 등록 처리 */
    @PostMapping("/goal/save")
    public String saveGoalAsset(@ModelAttribute AssetForm assetForm, HttpSession session) {
        LoginEntity loginUser = (LoginEntity) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        assetService.saveGoalAsset(loginUser.getId(), assetForm);
        return "redirect:/target"; // 등록 후 목표 트래킹 페이지로 이동
    }

    // ✅ 거래 내역 페이지 (페이징 15개씩)
    @GetMapping("/transaction/all")
    public String showTransactionHistory(
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        LoginEntity loginUser = (LoginEntity) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        int size = 15; // ✅ 한 페이지당 15개
        Page<TransactionEntity> transactionPage =
                transactionService.getTransactionsByUser(loginUser, page, size);

        // ✅ 모델에 값 정확히 담기
        model.addAttribute("transactions", transactionPage.getContent());
        model.addAttribute("currentPage", transactionPage.getNumber());
        model.addAttribute("totalPages", transactionPage.getTotalPages());
        model.addAttribute("hasNext", transactionPage.hasNext());
        model.addAttribute("hasPrev", transactionPage.hasPrevious());
        model.addAttribute("userName", loginUser.getName());

        return "Asset/asset-transaction";
    }


}
