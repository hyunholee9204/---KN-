package com.example.CapstonProject0.Repository;

import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Entity.TargetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetRepository extends JpaRepository<TargetEntity, Long> {
    List<TargetEntity> findByUser(LoginEntity user);
    List<TargetEntity> findByUserOrderByIdDesc(LoginEntity user);
}
