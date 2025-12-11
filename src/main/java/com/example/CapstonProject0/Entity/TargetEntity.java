package com.example.CapstonProject0.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "target")
@Builder
public class TargetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private LoginEntity user;

    @Column(nullable = false)
    private Long goalAmount;   // 🎯 목표 금액

    @Column(nullable = false)
    private Long totalAmount = 0L;   // 💰 지금까지 모인 금액 (목표전용 자산 합계)

    private String title;           // 목표 이름
    private String description;     // 세부 내용

    private LocalDate startDate;    // 시작일
    private LocalDate endDate;      // 마감일

    @Column(name = "progress", nullable = false)
    private double progress = 0.0;  // ✅ 진행률

    private LocalDateTime createdAt;

    @Transient
    private long daysLeft;

    /** ✅ 자산과의 연관관계 설정 (목표 삭제 시 자산도 같이 삭제) **/
    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssetEntity> assets = new ArrayList<>();


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.totalAmount == null) this.totalAmount = 0L;
        updateProgress();
    }

    @PreUpdate
    public void preUpdate() {
        updateProgress();
    }

    // ✅ 진행률 계산 (totalAmount / goalAmount) * 100
    public void updateProgress() {
        if (goalAmount != null && goalAmount > 0) {
            this.progress = ((double) totalAmount / goalAmount) * 100.0;
        } else {
            this.progress = 0.0;
        }
    }

    public void setDaysLeft(long daysLeft) {
        this.daysLeft = daysLeft;
    }

    public long getDaysLeft() {
        if (endDate == null) return -1;
        LocalDate today = LocalDate.now();

        // 날짜만 비교하도록 보정
        long days = java.time.temporal.ChronoUnit.DAYS.between(today, endDate);
        return days > 0 ? days : 0;
    }

}
