package com.example.CapstonProject0.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "repayment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LoansEntity (대출 내역)와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private LoansEntity loan;

    private int installmentNo;      // 회차
    private LocalDate dueDate;      // 상환일
    private double principal;       // 원금
    private double interest;        // 이자
    private double totalAmount;     // 총 상환금 (원금 + 이자)
    private String status;          // 상태 (예정 / 완료)
}
