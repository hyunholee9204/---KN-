package com.example.CapstonProject0.Repository;

import com.example.CapstonProject0.Entity.TargetHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TargetHistoryRepository extends JpaRepository<TargetHistoryEntity, Long> {
    List<TargetHistoryEntity> findByTargetIdOrderByChangeDateDesc(Long targetId);
}
