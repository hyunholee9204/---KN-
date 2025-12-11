package com.example.CapstonProject0.Repository;

import com.example.CapstonProject0.Entity.AssetEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssetRepository extends JpaRepository<AssetEntity, Long> {
    List<AssetEntity> findAllByUserId(Long userId);

    boolean existsByUserId(Long userId);

    // 사용자의 자산 총합 계산
    @Query("SELECT SUM(a.amount) FROM AssetEntity a WHERE a.userId = :userId")
    Long findTotalAmountByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(a.amount) FROM AssetEntity a WHERE a.userId = :userId")
    Long sumAmountByUser(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM AssetEntity a WHERE a.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(a.amount) FROM AssetEntity a WHERE a.userId = :userId AND a.type = :type")
    Long sumAmountByUserAndType(@Param("userId") Long userId, @Param("type") String type);

    @Query("SELECT SUM(a.amount) FROM AssetEntity a WHERE a.userId = :userId AND a.type = '목표전용자산' AND a.target.id = :targetId")
    Long sumGoalAssetsByTarget(@Param("userId") Long userId, @Param("targetId") Long targetId);

    @Query("SELECT SUM(a.amount) FROM AssetEntity a WHERE a.target.id = :targetId")
    Long sumByTargetId(@Param("targetId") Long targetId);

}
