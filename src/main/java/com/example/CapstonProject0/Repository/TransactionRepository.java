package com.example.CapstonProject0.Repository;

import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByUserOrderByTimestampDesc(LoginEntity user);
    Page<TransactionEntity> findByUserOrderByTimestampDesc(LoginEntity user, Pageable pageable);
}
