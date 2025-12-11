package com.example.CapstonProject0.Service;

import com.example.CapstonProject0.DTO.AssetForm;
import com.example.CapstonProject0.DTO.AssetTotalForm;
import com.example.CapstonProject0.Entity.AssetEntity;
import com.example.CapstonProject0.Entity.AssetHistoryEntity;
import com.example.CapstonProject0.Entity.LoginEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface AssetService {
    void registerAsset(Long userId, AssetForm form);
    boolean hasAsset(Long userId);
    int getTotalAsset(Long userId);
    AssetEntity findById(Long id);
    void updateAsset(Long id, String type, int amount);
    void recordMonthlyAsset(LoginEntity user);

    List<AssetEntity> findByUserId(Long userId);

    void deleteById(Long Id);

    List<AssetTotalForm> getGroupedAssetsByUser(Long userId);
    List<AssetHistoryEntity> getMonthlyHistory(LoginEntity user);
    void saveGoalAsset(Long userId, AssetForm form);
    Page<AssetHistoryEntity> getPagedHistory(LoginEntity user, int page, int size);
}

