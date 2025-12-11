package com.example.CapstonProject0.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
@AllArgsConstructor
public class RepaymentForm {
    private int installmentNo;   // 회차
    private LocalDate dueDate;     // 상환일
    private double principal;   // 원금
    private double interest;    // 이자
    private double totalAmount;       // 총액
    private String status;      // 상태 (예: 완료, 예정)

    // Getter/Setter
    public int getInstallmentNo() { return installmentNo; }
    public void setInstallmentNo(int installmentNo) { this.installmentNo = installmentNo; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public double getPrincipal() { return principal; }
    public void setPrincipal(double principal) { this.principal = principal; }

    public double getInterest() { return interest; }
    public void setInterest(double interest) { this.interest = interest; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
