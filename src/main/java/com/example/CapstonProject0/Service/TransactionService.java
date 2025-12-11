package com.example.CapstonProject0.Service;

import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Entity.TransactionEntity;
import com.example.CapstonProject0.Repository.LoginRepository;
import com.example.CapstonProject0.Repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final LoginRepository loginRepository;

    public TransactionService(TransactionRepository transactionRepository, LoginRepository loginRepository) {
        this.transactionRepository = transactionRepository;
        this.loginRepository = loginRepository;
    }

    // ✅ 거래 로그 저장
    public void logTransaction(LoginEntity user, String type, int amount, String action) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setUser(user);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setAction(action);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    public void logTransactionByUserId(Long userId, String type, int amount, String action) {
        LoginEntity user = loginRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음"));
        logTransaction(user, type, amount, action);
    }

    // ✅ 기존: 전체 내역 조회 (페이징 없음)
    public List<TransactionEntity> getTransactionsForUser(LoginEntity user) {
        return transactionRepository.findByUserOrderByTimestampDesc(user);
    }

    // ✅ 추가: 페이징 지원 내역 조회 (최신순, 20개씩)
    public Page<TransactionEntity> getTransactionsByUser(LoginEntity user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findByUserOrderByTimestampDesc(user, pageable);
    }
}
