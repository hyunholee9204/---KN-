package com.example.CapstonProject0.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoansEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private LoginEntity user;

    private String lender;         // 대출 기관

    @Convert(converter = LoanTypeConverter.class)
    private LoanType loanType;     // 대출 종류 (Enum)

    private Long principal;        // 대출 금액
    private double interestRate;   // 이자율 (%)
    private LocalDate startDate;   // 시작일
    private LocalDate endDate;     // 종료일
    private int totalInstallments; // 상환 횟수
    private String purpose;        // 대출 목적

    private LocalDateTime createdAt;
    private int gracePeriod;        // ✅ 거치 기간 (개월)
    private String repaymentType;   // ✅ 상환 방식 (원리금균등 / 원금균등 / 만기일시상환)

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepaymentEntity> repayments = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
