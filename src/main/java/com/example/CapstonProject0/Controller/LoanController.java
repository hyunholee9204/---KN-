package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.DTO.LoanForm;
import com.example.CapstonProject0.DTO.RepaymentForm;
import com.example.CapstonProject0.Entity.LoanType;
import com.example.CapstonProject0.Entity.LoansEntity;
import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Entity.RepaymentEntity;
import com.example.CapstonProject0.Repository.RepaymentRepository;
import com.example.CapstonProject0.Service.LoanService;
import com.example.CapstonProject0.Service.RepaymentService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("loans")
public class LoanController {

    private final LoanService loanService;
    private final RepaymentService repaymentService; // ✅ 추가
    private final RepaymentRepository repaymentRepository;

    public LoanController(LoanService loanService, RepaymentService repaymentService,
                          RepaymentRepository repaymentRepository) {
        this.loanService = loanService;
        this.repaymentService = repaymentService;
        this.repaymentRepository = repaymentRepository;
    }

    /** ✅ 대출 현황 페이지 */
    @GetMapping("")
    public String loanStatus(HttpSession session, Model model) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        List<LoansEntity> loans = loanService.getLoansByUser(user);
        Long totalLoanAmount = loanService.getTotalLoanAmount(user);
        String formattedAmount = String.format("%,d", totalLoanAmount);

        List<LoanForm> loanForms = loans.stream().map(LoanForm::new).toList();

        double avgInterestRate = loans.isEmpty()
                ? 0.0
                : loans.stream().mapToDouble(LoansEntity::getInterestRate).average().orElse(0.0);

        model.addAttribute("loans", loanForms);
        model.addAttribute("totalLoanAmount", formattedAmount);
        model.addAttribute("avgInterestRate", avgInterestRate);

        return "Loans/Loans_Management";
    }

    /** ✅ 대출 등록 폼 */
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("loan", new LoansEntity());
        model.addAttribute("loanTypes", LoanType.values());
        return "Loans/Loans-register";
    }

    /** ✅ 대출 등록 처리 */
    @PostMapping("/register")
    public String register(@ModelAttribute LoansEntity loan, HttpSession session) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        loan.setUser(user);
        loanService.registerLoan(loan);
        return "redirect:/loans";
    }

    /** ✅ 대출 수정 폼 */
    @GetMapping("/edit/{id}")
    public String editLoanForm(@PathVariable("id") Long id, HttpSession session, Model model) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        LoansEntity loan = loanService.findById(id);
        if (loan == null || !loan.getUser().getId().equals(user.getId())) {
            return "redirect:/loans";
        }

        model.addAttribute("loan", loan);
        model.addAttribute("loanTypes", LoanType.values());
        return "Loans/Loans-edit";
    }

    /** ✅ 대출 수정 처리 */
    @PostMapping("/update/{id}")
    public String updateLoan(@PathVariable("id") Long id, @ModelAttribute LoansEntity loan, HttpSession session) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        loan.setUser(user);
        loanService.updateLoan(id, loan);
        return "redirect:/loans";
    }

    @GetMapping("/delete/{id}")
    public String deleteLoan(@PathVariable("id") Long id, HttpSession session) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        loanService.deleteLoan(id);
        return "redirect:/loans";
    }

    /** ✅ 1. 계산기 페이지 열기 (GET 요청) */
    @GetMapping("/calculate")
    public String showInterestCalculator(HttpSession session, Model model) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        List<LoansEntity> loans = loanService.getLoansByUser(user);
        Long totalLoanAmount = loanService.getTotalLoanAmount(user);
        String formattedAmount = String.format("%,d", totalLoanAmount);
        List<LoanForm> loanForms = loans.stream().map(LoanForm::new).toList();

        model.addAttribute("loans", loanForms);
        model.addAttribute("totalLoanAmount", formattedAmount);
        return "Loans/Loans-calculate";
    }

    @PostMapping("/calculate")
    public String calculateInterest(
            @RequestParam("principal") double principal,
            @RequestParam("annualRate") double annualRate,
            @RequestParam("periodMonths") int periodMonths,
            @RequestParam(value = "gracePeriod", defaultValue = "0") int gracePeriod,
            @RequestParam("method") String method,
            HttpSession session,
            Model model) {

        // ✅ 로그인 체크
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        // ✅ 단위 변환 (만원 → 원)
        principal *= 10000;

        // ✅ 기본 변수 설정
        double monthlyRate = annualRate / 100 / 12; // 월 이율
        int repaymentMonths = Math.max(1, periodMonths - gracePeriod); // 최소 1개월 보정

        double monthlyPayment = 0;
        double totalRepayment = 0;
        double totalInterest = 0;
        Double postGraceMonthly = null;

        switch (method) {
            case "equalPrincipalInterest": // ✅ 원리금균등상환
                monthlyPayment = (principal * monthlyRate) /
                        (1 - Math.pow(1 + monthlyRate, -repaymentMonths));
                totalRepayment = monthlyPayment * repaymentMonths;
                totalInterest = totalRepayment - principal;

                if (gracePeriod > 0) {
                    postGraceMonthly = monthlyPayment;
                    monthlyPayment = principal * monthlyRate; // 거치기간: 이자만 납부
                }
                break;

            case "equalPrincipal": // ✅ 원금균등상환
                double principalPart = principal / repaymentMonths;
                totalInterest = 0;

                for (int i = 0; i < repaymentMonths; i++) {
                    double interest = (principal - principalPart * i) * monthlyRate;
                    totalInterest += interest;
                }

                totalRepayment = totalInterest + principal;
                monthlyPayment = principalPart + principal * monthlyRate; // 첫 달 기준

                if (gracePeriod > 0) {
                    postGraceMonthly = monthlyPayment;
                    monthlyPayment = principal * monthlyRate;
                }
                break;

            case "lumpSum": // ✅ 만기일시상환
                monthlyPayment = principal * monthlyRate;
                totalInterest = monthlyPayment * periodMonths;
                totalRepayment = principal + totalInterest;
                break;
        }

        // ✅ View로 전달할 데이터
        model.addAttribute("method", method);
        model.addAttribute("principalAmount", principal);
        model.addAttribute("monthlyPayment", monthlyPayment);
        model.addAttribute("totalRepayment", totalRepayment);
        model.addAttribute("totalInterest", totalInterest);
        model.addAttribute("gracePeriod", gracePeriod);
        model.addAttribute("repaymentMonths", repaymentMonths);
        model.addAttribute("postGraceMonthly", postGraceMonthly);

        return "Loans/Loans-calculate";
    }

    @GetMapping("/schedule")
    public String loanSchedule(HttpSession session, Model model) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        loanService.updateRepaymentStatus(); // ✅ 상태 갱신

        // 🔹 전체 대출 목록 (비어있으면 빈 리스트라도 전달)
        List<LoansEntity> loans = loanService.getLoansByUser(user);
        model.addAttribute("loans", loans != null ? loans : List.of());

        if (loans == null || loans.isEmpty()) {
            model.addAttribute("schedule", List.of());
            model.addAttribute("nextDateText", "-");
            model.addAttribute("remainingText", "-");
            return "Loans/Loans-schedule";
        }

        // ✅ 기본 선택된 첫 번째 대출
        LoansEntity firstLoan = loans.get(0);
        List<RepaymentForm> schedule = loanService.generateSchedule(firstLoan);
        if (schedule == null) schedule = new ArrayList<>();

        // ✅ 총 금액 계산
        double totalRepay = schedule.stream().mapToDouble(RepaymentForm::getTotalAmount).sum();
        double totalInterest = schedule.stream().mapToDouble(RepaymentForm::getInterest).sum();

        // ✅ 다음 상환일 및 상태 계산
        LocalDate nextDate = null;
        boolean allCompleted = true;
        int remainingCount = 0;

        for (RepaymentForm r : schedule) {
            if (r == null || r.getStatus() == null) continue;

            // "완료"가 아닌 회차는 남은 상환으로 간주
            if (!"완료".equals(r.getStatus())) {
                allCompleted = false;
                remainingCount++;

                // ✅ 가장 빠른 예정 상환일 선택 (오늘 이후이면서 가장 이른 날짜)
                if (nextDate == null || r.getDueDate().isBefore(nextDate)) {
                    nextDate = r.getDueDate();
                }
            }
        }

        // ✅ “모두 상환 완료” 또는 “예정” 텍스트 설정
        String nextDateText = allCompleted
                ? "모두 상환 완료"
                : (nextDate != null ? nextDate.toString() : "-");
        String remainingText = allCompleted
                ? "0회 (완료)"
                : (remainingCount + "회");

        // ✅ Model에 전달
        model.addAttribute("selectedLoan", firstLoan);
        model.addAttribute("schedule", schedule);
        model.addAttribute("totalRepay", totalRepay);
        model.addAttribute("totalInterest", totalInterest);
        model.addAttribute("nextDateText", nextDateText);
        model.addAttribute("remainingText", remainingText);
        model.addAttribute("nextDate", nextDate);
        model.addAttribute("allCompleted", allCompleted);
        model.addAttribute("remainingCount", remainingCount);

        return "Loans/Loans-schedule";
    }

    @GetMapping("/schedule/{loanId}")
    public String loanScheduleDetail(@PathVariable("loanId") Long loanId, HttpSession session, Model model) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        List<LoansEntity> loans = loanService.getLoansByUser(user);
        model.addAttribute("loans", loans != null ? loans : List.of());

        LoansEntity loan = loanService.findById(loanId);
        if (loan == null) {
            model.addAttribute("schedule", List.of());
            model.addAttribute("nextDateText", "-");
            model.addAttribute("remainingText", "-");
            return "Loans/Loans-schedule";
        }

        List<RepaymentForm> schedule = loanService.generateSchedule(loan);
        if (schedule == null) schedule = new ArrayList<>();

        double totalRepay = schedule.stream().mapToDouble(RepaymentForm::getTotalAmount).sum();
        double totalInterest = schedule.stream().mapToDouble(RepaymentForm::getInterest).sum();

        // ✅ 다음 상환일 및 상태 계산
        LocalDate nextDate = null;
        boolean allCompleted = true;
        int remainingCount = 0;

        for (RepaymentForm r : schedule) {
            if (r == null || r.getStatus() == null) continue;

            if (!"완료".equals(r.getStatus())) {
                allCompleted = false;
                remainingCount++;

                if (nextDate == null || r.getDueDate().isBefore(nextDate)) {
                    nextDate = r.getDueDate();
                }
            }
        }

        String nextDateText = allCompleted
                ? "모두 상환 완료"
                : (nextDate != null ? nextDate.toString() : "-");
        String remainingText = allCompleted
                ? "0회 (완료)"
                : (remainingCount + "회");

        // ✅ Model에 전달
        model.addAttribute("selectedLoan", loan);
        model.addAttribute("schedule", schedule);
        model.addAttribute("totalRepay", totalRepay);
        model.addAttribute("totalInterest", totalInterest);
        model.addAttribute("nextDateText", nextDateText);
        model.addAttribute("remainingText", remainingText);
        model.addAttribute("nextDate", nextDate);
        model.addAttribute("allCompleted", allCompleted);
        model.addAttribute("remainingCount", remainingCount);

        return "Loans/Loans-schedule";
    }

    @GetMapping("/generate-missing")
    @Transactional
    public String generateMissingRepayments() {
        List<LoansEntity> loans = loanService.getAllLoans();
        int created = 0;

        for (LoansEntity loan : loans) {
            if (loan.getRepayments() == null || loan.getRepayments().isEmpty()) {

                List<RepaymentEntity> schedule = loanService.generateRepaymentSchedule(loan);
                schedule.forEach(r -> r.setLoan(loan));

                // ✅ 교체 대신 addAll로 기존 리스트에 추가
                if (loan.getRepayments() == null) {
                    loan.setRepayments(new ArrayList<>());
                }
                loan.getRepayments().addAll(schedule);

                // ✅ 직접 저장 (별도 saveRepayments 유지 가능)
                loanService.saveRepayments(schedule);
                created++;

                System.out.println("✅ 상환 일정 생성 완료: loanId=" + loan.getId());
            }
        }

        System.out.println("✅ 총 " + created + "개의 대출에 상환 일정 생성 완료");
        return "redirect:/loans/schedule";
    }


}
