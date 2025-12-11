package com.example.CapstonProject0.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "finance_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FinanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // 데이터 종류 (USD, KOSPI 등)
    private String code;
    private Double value; // 실제 값
    private LocalDateTime timestamp; // 저장 시각
}
