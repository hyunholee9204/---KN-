package com.example.CapstonProject0.Service;

import com.example.CapstonProject0.DTO.AssetForm;
import com.example.CapstonProject0.DTO.AssetTotalForm;
import com.example.CapstonProject0.Entity.*;
import com.example.CapstonProject0.Repository.AssetHistoryRepository;
import com.example.CapstonProject0.Repository.AssetRepository;
import com.example.CapstonProject0.Repository.TargetHistoryRepository;
import com.example.CapstonProject0.Repository.TargetRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final TransactionService transactionService;
    private final AssetHistoryRepository assetHistoryRepository;
    private final TargetRepository targetRepository;
    private final TargetHistoryRepository targetHistoryRepository;

    public AssetServiceImpl(AssetRepository assetRepository,
                            TransactionService transactionService,
                            AssetHistoryRepository assetHistoryRepository,
                            TargetRepository targetRepository,
                            TargetHistoryRepository targetHistoryRepository) {
        this.assetRepository = assetRepository;
        this.transactionService = transactionService;
        this.assetHistoryRepository = assetHistoryRepository;
        this.targetRepository = targetRepository;
        this.targetHistoryRepository = targetHistoryRepository;
    }

    // ✅ 자산 등록
    @Override
    public void registerAsset(Long userId, AssetForm form) {
        AssetEntity asset = new AssetEntity();
        asset.setUserId(userId);
        asset.setAmount(form.getAmount());
        asset.setType(form.getType());
        assetRepository.save(asset);
        transactionService.logTransactionByUserId(userId, form.getType(), form.getAmount(), "추가");

        LoginEntity user = new LoginEntity();
        user.setId(userId);
        recordMonthlyAsset(user); // ✅ 월별 자산 갱신
    }


    // ✅ 자산 존재 여부
    @Override
    public boolean hasAsset(Long userId) {
        return assetRepository.existsByUserId(userId);
    }

    // ✅ 자산 총합 조회
    @Override
    public int getTotalAsset(Long userId) {
        Long result = assetRepository.findTotalAmountByUserId(userId);
        return (result != null) ? result.intValue() : 0;
    }


    // ✅ 사용자별 자산 목록
    @Override
    public List<AssetEntity> findByUserId(Long userId) {
        return assetRepository.findAllByUserId(userId);
    }

    // ✅ 자산 유형별 그룹핑 합계
    @Override
    public List<AssetTotalForm> getGroupedAssetsByUser(Long userId) {
        List<AssetEntity> assets = findByUserId(userId);
        Map<String, Long> grouped = new HashMap<>();

        for (AssetEntity asset : assets) {
            String type = asset.getType();
            int amount = asset.getAmount();
            grouped.put(type, grouped.getOrDefault(type, 0L) + amount);
        }

        return grouped.entrySet().stream()
                .map(entry -> new AssetTotalForm(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    // ✅ 단일 자산 조회
    @Override
    public AssetEntity findById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("자산이 존재하지 않습니다."));
    }

    // ✅ 자산 수정
    @Override
    public void updateAsset(Long id, String type, int amount) {
        AssetEntity asset = findById(id);
        asset.setType(type);
        asset.setAmount(amount);
        assetRepository.save(asset);
        transactionService.logTransactionByUserId(asset.getUserId(), type, amount, "수정");

        LoginEntity user = new LoginEntity();
        user.setId(asset.getUserId());
        recordMonthlyAsset(user); // ✅ 월별 자산 갱신
    }


    // ✅ 자산 삭제
    @Override
    public void deleteById(Long id) {
        AssetEntity asset = findById(id);
        Long userId = asset.getUserId();
        assetRepository.deleteById(id);
        transactionService.logTransactionByUserId(userId, asset.getType(), asset.getAmount(), "삭제");

        LoginEntity user = new LoginEntity();
        user.setId(userId);
        recordMonthlyAsset(user); // ✅ 월별 자산 갱신
    }

    @Override
    public void recordMonthlyAsset(LoginEntity user) {
        // 현재 자산 총합 (Asset 테이블 기준)
        Long total = assetRepository.sumAmountByUser(user.getId());
        if (total == null) total = 0L;

        // 이번 달 1일 기준으로만 기록
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);

        // ✅ 이번 달 데이터가 이미 있으면 "update", 없으면 "insert"
        AssetHistoryEntity existing = assetHistoryRepository.findByUserAndRecordMonth(user, firstDayOfMonth);

        if (existing == null) {
            AssetHistoryEntity history = AssetHistoryEntity.builder()
                    .user(user)
                    .recordMonth(firstDayOfMonth)
                    .totalAmount(total)
                    .build();
            assetHistoryRepository.save(history);
            System.out.println("[AssetHistory] 새 월 기록 생성: " + firstDayOfMonth + " / " + total);
        } else {
            existing.setTotalAmount(total);
            assetHistoryRepository.save(existing);
            System.out.println("[AssetHistory] 월 기록 갱신: " + firstDayOfMonth + " / " + total);
        }
    }


    // ✅ 사용자 월별 기록 조회
    public List<AssetHistoryEntity> getMonthlyHistory(LoginEntity user) {
        return assetHistoryRepository.findByUserOrderByRecordMonthAsc(user);
    }

    @Override
    public void saveGoalAsset(Long userId, AssetForm form) {
        // 1️⃣ 자산 생성 및 설정
        AssetEntity asset = new AssetEntity();
        asset.setUserId(userId);
        asset.setType("목표전용자산");
        asset.setAmount(form.getAmount());

        // 2️⃣ [핵심] targetId 기반으로 목표 연결
        if (form.getTargetId() != null) {
            TargetEntity target = targetRepository.findById(form.getTargetId()).orElse(null);
            if (target != null) {
                asset.setTarget(target);
            }
        }

        assetRepository.save(asset);
        // ✅ 목표 내역 자동 기록
        if (asset.getTarget() != null) {
            TargetHistoryEntity history = TargetHistoryEntity.builder()
                    .target(asset.getTarget())
                    .changeAmount((long) form.getAmount())
                    .changeType("deposit") // 자산 추가이므로 입금 처리
                    .changeDate(LocalDate.now())
                    .memo("목표용 자산 자동 등록")
                    .build();

            targetHistoryRepository.save(history);
            System.out.println("✅ TargetHistory 자동 저장 완료: " + history.getChangeAmount() + "원");
        }


        // 3️⃣ 거래 기록
        transactionService.logTransactionByUserId(userId, "목표전용자산", form.getAmount(), "목표용 추가");

        // 4️⃣ 월별 자산 기록
        LoginEntity user = new LoginEntity();
        user.setId(userId);
        recordMonthlyAsset(user);

        // 5️⃣ 목표 진행률 업데이트
        if (form.getTargetId() != null) {
            updateTargetProgress(userId, form.getTargetId());
        }
    }

    @Transactional
    private void updateTargetProgress(Long userId, Long targetId) {
        TargetEntity target = targetRepository.findById(targetId).orElse(null);
        if (target == null) {
            System.out.println("🚫 대상 목표 없음 (targetId=" + targetId + ")");
            return;
        }

        Long goalAmount = target.getGoalAmount();
        if (goalAmount == null || goalAmount == 0L) {
            target.setProgress(0);
            target.setTotalAmount(0L);
            targetRepository.save(target);
            System.out.println("⚠️ goalAmount가 null 또는 0 → progress=0 처리");
            return;
        }

        // ✅ 특정 목표(target_id)에 연결된 자산 합계로 변경
        Long totalGoalAssets = assetRepository.sumByTargetId(targetId);
        if (totalGoalAssets == null) totalGoalAssets = 0L;

        target.setTotalAmount(totalGoalAssets);
        double progress = ((double) totalGoalAssets / goalAmount) * 100;
        target.setProgress(Math.min(progress, 100.0));

        targetRepository.save(target);

        System.out.printf("✅ 목표(%s) 업데이트 완료: total=%d, goal=%d, progress=%.2f%%%n",
                target.getTitle(), totalGoalAssets, goalAmount, progress);
    }

    // ✅ 페이지네이션용 월별 자산 내역 조회 (최신순)
    public Page<AssetHistoryEntity> getPagedHistory(LoginEntity user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return assetHistoryRepository.findByUserOrderByRecordMonthDesc(user, pageable);
    }
}
