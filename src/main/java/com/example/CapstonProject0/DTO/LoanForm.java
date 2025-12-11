package com.example.CapstonProject0.DTO;

import com.example.CapstonProject0.Entity.LoansEntity;

import java.time.LocalDate;

public class LoanForm {
    private Long id;
    private String lender;
    private String loanType;
    private Long principal;
    private double interestRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalInstallments;
    private String formattedAmount;

    public LoanForm(LoansEntity loan) {
        this.id = loan.getId();
        this.lender = loan.getLender();
        this.loanType = loan.getLoanType().getDescription();
        this.principal = loan.getPrincipal();
        this.interestRate = loan.getInterestRate();
        this.startDate = loan.getStartDate();
        this.endDate = loan.getEndDate();
        this.totalInstallments = loan.getTotalInstallments();
        this.formattedAmount = String.format("₩%,d", loan.getPrincipal());
    }

    public Long getId() { return id; }
    public String getLender() { return lender; }
    public String getLoanType() { return loanType; }
    public Long getPrincipal() { return principal; }
    public double getInterestRate() { return interestRate; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public int getTotalInstallments() { return totalInstallments; }
    public String getFormattedAmount() { return formattedAmount; }
}
