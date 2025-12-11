package com.example.CapstonProject0.Repository;

import com.example.CapstonProject0.Entity.RepaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RepaymentRepository extends JpaRepository<RepaymentEntity, Long> {

    // ✅ 확실히 동작하는 명시적 쿼리 방식
    @Query("SELECT r FROM RepaymentEntity r WHERE r.loan.id = :loanId ORDER BY r.installmentNo ASC")
    List<RepaymentEntity> findByLoanId(@Param("loanId") Long loanId);
}
