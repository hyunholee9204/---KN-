package com.example.CapstonProject0.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user"})
@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "asset_history")
public class AssetHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private LoginEntity user;

    private LocalDate recordMonth;     // 기록된 월 (예: 2025-10-01)
    private Long totalAmount;          // 해당 월의 총 자산
    private LocalDateTime createdAt;   // 기록 생성 시각

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
