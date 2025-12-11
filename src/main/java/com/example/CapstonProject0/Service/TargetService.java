package com.example.CapstonProject0.Service;

import com.example.CapstonProject0.DTO.TargetHistoryForm;
import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Entity.TargetEntity;
import com.example.CapstonProject0.Entity.TargetHistoryEntity;
import com.example.CapstonProject0.Repository.AssetRepository;
import com.example.CapstonProject0.Repository.TargetHistoryRepository;
import com.example.CapstonProject0.Repository.TargetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TargetService {

    private final TargetRepository targetRepository;
    private final AssetRepository assetRepository;
    private final TargetHistoryRepository historyRepository;

    // ✅ 목표 저장 (totalAmount는 0부터 시작, 진행률 자동 계산)
    public void saveTarget(TargetEntity target) {
        if (target.getTotalAmount() == null) {
            target.setTotalAmount(0L);
        }

        if (target.getGoalAmount() != null && target.getGoalAmount() > 0) {
            double progress = ((double) target.getTotalAmount() / target.getGoalAmount()) * 100;
            target.setProgress(progress);
        } else {
            target.setProgress(0);
        }

        targetRepository.save(target);
    }


    public List<TargetEntity> getTargetsByUser(LoginEntity user) {
        List<TargetEntity> targets = targetRepository.findByUser(user);

        // ✅ 남은 기간 강제 계산
        for (TargetEntity t : targets) {
            if (t.getEndDate() != null) {
                long days = ChronoUnit.DAYS.between(LocalDate.now(), t.getEndDate());
                t.setDaysLeft(Math.max(days, 0));
            } else {
                t.setDaysLeft(0);
            }
        }

        return targets;
    }

    // ✅ 목표 수정
    public void updateTarget(Long id, TargetEntity updatedTarget, LoginEntity user) {
        TargetEntity existing = targetRepository.findById(id).orElse(null);
        if (existing != null && existing.getUser().getId().equals(user.getId())) {

            if (updatedTarget.getTitle() != null) existing.setTitle(updatedTarget.getTitle());
            if (updatedTarget.getDescription() != null) existing.setDescription(updatedTarget.getDescription());
            if (updatedTarget.getGoalAmount() != null) existing.setGoalAmount(updatedTarget.getGoalAmount());
            if (updatedTarget.getStartDate() != null) existing.setStartDate(updatedTarget.getStartDate());
            if (updatedTarget.getEndDate() != null) existing.setEndDate(updatedTarget.getEndDate());

            // 진행률 재계산
            if (existing.getGoalAmount() != null && existing.getGoalAmount() > 0) {
                double progress = ((double) existing.getTotalAmount() / existing.getGoalAmount()) * 100;
                existing.setProgress(progress);
            } else {
                existing.setProgress(0);
            }

            targetRepository.save(existing);
        }
    }

    // ✅ 목표 삭제
    public void deleteTarget(Long id, LoginEntity user) {
        TargetEntity existing = targetRepository.findById(id).orElse(null);
        if (existing != null && existing.getUser().getId().equals(user.getId())) {
            targetRepository.delete(existing);
        }
    }

    // ✅ 목표 단일 조회
    public TargetEntity getTargetById(Long id) {
        return targetRepository.findById(id).orElse(null);
    }

    // ✅ 보기 좋은 문자열 형태 변환
    public List<String> getFormattedTargetsByUser(LoginEntity user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return targetRepository.findByUserOrderByIdDesc(user)
                .stream()
                .map(target -> {
                    String endDate = (target.getEndDate() != null)
                            ? target.getEndDate().format(formatter)
                            : "종료일 미정";
                    int progress = (int) target.getProgress();
                    return target.getTitle() + " (~" + endDate + ") - " + progress + "%";
                })
                .collect(Collectors.toList());
    }

    // ✅ 목표별 진행률과 현재금액 동기화
    @Transactional
    public void refreshTargetProgress(Long targetId) {
        TargetEntity target = targetRepository.findById(targetId).orElse(null);
        if (target == null) return;

        Long total = assetRepository.sumByTargetId(targetId);
        if (total == null) total = 0L;

        target.setTotalAmount(total);
        target.updateProgress();
        targetRepository.save(target);
    }

    // ✅ 목표 진행률 계산 (다른 곳에서도 재사용)
    public double calculateProgress(TargetEntity target) {
        if (target.getGoalAmount() == null || target.getGoalAmount() == 0) return 0;
        return Math.round((double) target.getTotalAmount() / target.getGoalAmount() * 100);
    }

    // ✅ 남은 일수 계산
    public long getDaysLeft(TargetEntity target) {
        if (target.getEndDate() == null) return 0;
        long days = ChronoUnit.DAYS.between(LocalDate.now(), target.getEndDate());
        return Math.max(days, 0);
    }


    // ✅ 목표별 내역 (진행 내역 테이블)
    public List<TargetHistoryEntity> getTargetHistory(Long targetId) {
        return historyRepository.findByTargetIdOrderByChangeDateDesc(targetId);
    }

    // ✅ 월별 저축 합계 (그래프용)
    public Map<String, Long> getMonthlyChangeData(Long targetId) {
        List<TargetHistoryEntity> historyList = getTargetHistory(targetId);
        Map<String, Long> monthlyData = new LinkedHashMap<>();

        for (TargetHistoryEntity h : historyList) {
            if (h.getChangeDate() == null) continue;
            String month = h.getChangeDate().getMonthValue() + "월";
            monthlyData.put(month, monthlyData.getOrDefault(month, 0L) + h.getChangeAmount());
        }

        return monthlyData;
    }

    public Map<String, Long> getBiweeklyChangeData(Long targetId) {
        List<TargetHistoryEntity> historyList = getTargetHistory(targetId);
        Map<String, Long> biweeklyData = new LinkedHashMap<>();

        if (historyList.isEmpty()) return biweeklyData;

        TargetEntity target = targetRepository.findById(targetId).orElse(null);
        if (target == null || target.getStartDate() == null) return biweeklyData;

        LocalDate start = target.getStartDate();
        LocalDate end = target.getEndDate() != null ? target.getEndDate() : LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd");

        LocalDate current = start;
        int periodIndex = 1;

        while (!current.isAfter(end)) {
            LocalDate next = current.plusDays(15);

            // ✅ lambda 밖에서 따로 final 복사
            LocalDate periodStart = current;
            LocalDate periodEnd = next;

            long sum = 0;
            for (TargetHistoryEntity h : historyList) {
                LocalDate d = h.getChangeDate();
                if ((d.isEqual(periodStart) || (d.isAfter(periodStart) && d.isBefore(periodEnd)))) {
                    sum += h.getChangeAmount();
                }
            }

            String label = "기간 " + periodIndex + " (" +
                    periodStart.format(fmt) + "~" + periodEnd.minusDays(1).format(fmt) + ")";
            biweeklyData.put(label, sum);

            current = next;
            periodIndex++;
        }

        return biweeklyData;
    }

    @Transactional
    public void addHistory(TargetHistoryForm form) {
        TargetEntity target = targetRepository.findById(form.getTargetId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 목표입니다."));

        TargetHistoryEntity history = TargetHistoryEntity.builder()
                .target(target)
                .changeAmount(form.getChangeAmount())
                .changeType(form.getChangeType())
                .changeDate(LocalDate.now())
                .memo(form.getMemo())
                .build();

        historyRepository.save(history);

        // ✅ 목표 총액 갱신
        if ("deposit".equals(form.getChangeType())) {
            target.setTotalAmount(target.getTotalAmount() + form.getChangeAmount());
        } else if ("withdraw".equals(form.getChangeType())) {
            target.setTotalAmount(target.getTotalAmount() - form.getChangeAmount());
        }

        target.updateProgress();
        targetRepository.save(target);
    }



}
