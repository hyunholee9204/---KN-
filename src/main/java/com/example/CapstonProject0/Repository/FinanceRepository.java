package com.example.CapstonProject0.Repository;

import com.example.CapstonProject0.Entity.FinanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface FinanceRepository extends JpaRepository<FinanceEntity, Long> {

    List<FinanceEntity> findByTypeAndCodeAndTimestampBetween(
            String type, String code, LocalDateTime start, LocalDateTime end);

    List<FinanceEntity> findTop2ByTypeAndCodeOrderByTimestampDesc(String type, String code);
}
