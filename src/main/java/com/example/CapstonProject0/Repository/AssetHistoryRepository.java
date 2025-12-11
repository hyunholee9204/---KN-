package com.example.CapstonProject0.Repository;

import com.example.CapstonProject0.Entity.AssetHistoryEntity;
import com.example.CapstonProject0.Entity.LoginEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AssetHistoryRepository extends JpaRepository<AssetHistoryEntity, Long> {
    AssetHistoryEntity findByUserAndRecordMonth(LoginEntity user, LocalDate recordMonth);
    List<AssetHistoryEntity> findByUserOrderByRecordMonthAsc(LoginEntity user);
    Page<AssetHistoryEntity> findByUserOrderByRecordMonthDesc(LoginEntity user, Pageable pageable);
}
