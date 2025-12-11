package com.example.CapstonProject0.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetHistoryForm {
    private Long targetId;       // 어떤 목표인지
    private Long changeAmount;   // 금액
    private String changeType;   // deposit / withdraw
    private String memo;         // 메모
}
