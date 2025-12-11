package com.example.CapstonProject0.Repository;

import com.example.CapstonProject0.Entity.LoansEntity;
import com.example.CapstonProject0.Entity.LoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoansEntity, Long> {

    /** 특정 사용자별 대출 조회 */
    List<LoansEntity> findByUser(LoginEntity user);

    /** 특정 사용자 + 대출종류별 조회 (필요할 경우) */
    List<LoansEntity> findByUserAndLoanType(LoginEntity user, String loanType);
}
