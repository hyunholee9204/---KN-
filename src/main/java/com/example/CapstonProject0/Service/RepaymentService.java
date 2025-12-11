package com.example.CapstonProject0.Service;

import com.example.CapstonProject0.Entity.LoansEntity;
import com.example.CapstonProject0.Entity.RepaymentEntity;
import com.example.CapstonProject0.Repository.RepaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepaymentService {

    private final RepaymentRepository repaymentRepository;

    /** ✅ 특정 대출 ID 기준 상환내역 조회 */
    public List<RepaymentEntity> getRepaymentsByLoanId(Long loanId) {
        return repaymentRepository.findByLoanId(loanId);
    }

    /** ✅ 상환 상태 업데이트 (예정 → 완료 등) */
    @Transactional
    public void updateRepaymentStatus(Long repaymentId, String newStatus) {
        RepaymentEntity repayment = repaymentRepository.findById(repaymentId)
                .orElseThrow(() -> new IllegalArgumentException("상환 내역을 찾을 수 없습니다. id=" + repaymentId));

        repayment.setStatus(newStatus);
        repaymentRepository.save(repayment);
        System.out.println("🔄 Repayment 상태 변경 완료: " + repaymentId + " → " + newStatus);
    }

    /** ✅ 특정 대출의 상환 일정 전체 삭제 */
    @Transactional
    public void deleteAllByLoan(LoansEntity loan) {
        List<RepaymentEntity> list = repaymentRepository.findByLoanId(loan.getId());
        repaymentRepository.deleteAll(list);
        System.out.println("🗑️ 상환 일정 전체 삭제 완료 (loanId=" + loan.getId() + ")");
    }
}
