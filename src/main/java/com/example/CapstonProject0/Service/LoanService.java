package com.example.CapstonProject0.Service;

import com.example.CapstonProject0.DTO.RepaymentForm;
import com.example.CapstonProject0.Entity.LoansEntity;
import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Entity.RepaymentEntity;
import com.example.CapstonProject0.Repository.LoanRepository;
import com.example.CapstonProject0.Repository.RepaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final RepaymentRepository repaymentRepository;

    public LoanService(LoanRepository loanRepository, RepaymentRepository repaymentRepository) {
        this.loanRepository = loanRepository;
        this.repaymentRepository = repaymentRepository;
    }

    /** ✅ 특정 유저의 모든 대출 내역 조회 */
    public List<LoansEntity> getLoansByUser(LoginEntity user) {
        return loanRepository.findByUser(user);
    }

    /** ✅ 특정 유저의 총 대출금액 계산 */
    public Long getTotalLoanAmount(LoginEntity user) {
        return loanRepository.findByUser(user)
                .stream()
                .mapToLong(LoansEntity::getPrincipal)
                .sum();
    }

    @Transactional
    public LoansEntity registerLoan(LoansEntity loan) {

        LoansEntity savedLoan = loanRepository.saveAndFlush(loan);
        System.out.println("✅ Loan saved. loanId = " + savedLoan.getId());

        List<RepaymentEntity> schedule = generateRepaymentSchedule(savedLoan);
        schedule.forEach(r -> r.setLoan(savedLoan));
        savedLoan.getRepayments().clear();
        savedLoan.getRepayments().addAll(schedule);

        return loanRepository.save(savedLoan);
    }

    /** ✅ 대출 수정 */
    @Transactional
    public LoansEntity updateLoan(Long id, LoansEntity updatedLoan) {
        LoansEntity existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("대출 내역이 존재하지 않습니다. id=" + id));

        existingLoan.setLender(updatedLoan.getLender());
        existingLoan.setLoanType(updatedLoan.getLoanType());
        existingLoan.setPrincipal(updatedLoan.getPrincipal());
        existingLoan.setInterestRate(updatedLoan.getInterestRate());
        existingLoan.setStartDate(updatedLoan.getStartDate());
        existingLoan.setEndDate(updatedLoan.getEndDate());
        existingLoan.setTotalInstallments(updatedLoan.getTotalInstallments());
        existingLoan.setPurpose(updatedLoan.getPurpose());
        existingLoan.setGracePeriod(updatedLoan.getGracePeriod());
        existingLoan.setRepaymentType(updatedLoan.getRepaymentType());

        // ✅ 기존 상환내역 안전 삭제
        List<RepaymentEntity> oldRepayments = repaymentRepository.findByLoanId(id);
        repaymentRepository.deleteAllInBatch(oldRepayments);

        // ✅ 새 일정 생성
        List<RepaymentEntity> newSchedule = generateRepaymentSchedule(existingLoan);
        existingLoan.getRepayments().clear();
        existingLoan.getRepayments().addAll(newSchedule);

        loanRepository.saveAndFlush(existingLoan);
        repaymentRepository.saveAll(newSchedule);

        System.out.println("♻️ Loan 수정 및 상환 일정 재생성 완료 (loanId=" + id + ")");
        return existingLoan;
    }

    /** ✅ 대출 삭제 */
    @Transactional
    public void deleteLoan(Long id) {
        LoansEntity loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 대출을 찾을 수 없습니다. id=" + id));

        if (loan.getRepayments() != null) {
            loan.getRepayments().size();
        }

        loanRepository.delete(loan);
        System.out.println("🗑️ Loan 및 관련 상환 일정 삭제 완료 (loanId=" + id + ")");
    }

    /** ✅ 특정 대출 조회 */
    public LoansEntity findById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 대출 정보를 찾을 수 없습니다. id=" + id));
    }

    /** ✅ 상환 일정 생성 로직 */
    public List<RepaymentEntity> generateRepaymentSchedule(LoansEntity loan) {
        double principal = loan.getPrincipal();
        double annualRate = loan.getInterestRate() / 100.0;
        int months = loan.getTotalInstallments();
        int gracePeriod = loan.getGracePeriod();

        String type = loan.getRepaymentType();
        if (type == null || type.isBlank()) {
            type = "원리금균등";
        }

        LocalDate startDate = loan.getStartDate() != null ? loan.getStartDate() : LocalDate.now();
        List<RepaymentEntity> schedule = new ArrayList<>();

        double monthlyPrincipal = principal / Math.max(1, (months - gracePeriod));

        for (int i = 1; i <= months; i++) {
            LocalDate dueDate = startDate.plusMonths(i);
            double principalPart = 0;
            double interestPart = 0;

            if (i <= gracePeriod) {
                interestPart = principal * (annualRate / 12);
            } else {
                switch (type) {
                    case "원리금균등" -> {
                        double monthlyRate = annualRate / 12;
                        double totalPayment = (principal * monthlyRate * Math.pow(1 + monthlyRate, months - gracePeriod))
                                / (Math.pow(1 + monthlyRate, months - gracePeriod) - 1);
                        interestPart = principal * monthlyRate;
                        principalPart = totalPayment - interestPart;
                        principal -= principalPart;
                    }
                    case "원금균등" -> {
                        principalPart = monthlyPrincipal;
                        interestPart = principal * (annualRate / 12);
                        principal -= principalPart;
                    }
                    case "만기일시상환" -> {
                        if (i == months) principalPart = principal;
                        interestPart = principal * (annualRate / 12);
                    }
                    default -> {
                        principalPart = monthlyPrincipal;
                        interestPart = principal * (annualRate / 12);
                        principal -= principalPart;
                    }
                }
            }

            double total = principalPart + interestPart;

            RepaymentEntity repayment = new RepaymentEntity();
            repayment.setLoan(loan);
            repayment.setInstallmentNo(i);
            repayment.setDueDate(dueDate);
            repayment.setPrincipal(principalPart);
            repayment.setInterest(interestPart);
            repayment.setTotalAmount(total);
            repayment.setStatus("예정");

            schedule.add(repayment);
        }

        return schedule;
    }

    /** ✅ 상환 일정 DTO 변환 */
    public List<RepaymentForm> generateSchedule(LoansEntity loan) {
        if (loan == null || loan.getId() == null) return new ArrayList<>();

        List<RepaymentEntity> list = repaymentRepository.findByLoanId(loan.getId());
        List<RepaymentForm> result = new ArrayList<>();

        for (RepaymentEntity r : list) {
            if (r == null) continue;
            result.add(new RepaymentForm(
                    r.getInstallmentNo(),
                    r.getDueDate(),
                    r.getPrincipal(),
                    r.getInterest(),
                    r.getTotalAmount(),
                    r.getStatus()
            ));
        }
        return result;
    }

    // ✅ 모든 대출 조회용 (generateMissingRepayments에서 사용)
    public List<LoansEntity> getAllLoans() {
        return loanRepository.findAll();
    }

    // ✅ 상환 일정 저장용 (generateMissingRepayments에서 사용)
    @Transactional
    public void saveRepayments(List<RepaymentEntity> repayments) {
        repaymentRepository.saveAll(repayments);
    }

    @Transactional
    public void updateRepaymentStatus() {
        LocalDate today = LocalDate.now();
        List<RepaymentEntity> repayments = repaymentRepository.findAll();

        for (RepaymentEntity r : repayments) {
            if (r.getDueDate().isBefore(today)) {
                r.setStatus("완료");
            } else {
                r.setStatus("예정");
            }
        }

        repaymentRepository.saveAll(repayments);
    }

}
