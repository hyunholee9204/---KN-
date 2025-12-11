package com.example.CapstonProject0.Repository;

import com.example.CapstonProject0.Entity.UpdateEntity;
import com.example.CapstonProject0.Entity.UpdateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpdateRepository extends JpaRepository<UpdateEntity, Long> {
    Page<UpdateEntity> findAllByStatusOrderByPinnedDescReleaseDateDesc(
            UpdateStatus status, Pageable pageable);
}
