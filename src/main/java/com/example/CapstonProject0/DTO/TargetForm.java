package com.example.CapstonProject0.DTO;

import java.time.LocalDate;

public class TargetForm {

    private String title;          // 목표명
    private Long goalAmount;       // 목표 금액
    private LocalDate startDate;   // 시작일
    private LocalDate endDate;     // 종료일

    // ✅ Getter / Setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(Long goalAmount) {
        this.goalAmount = goalAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
