package com.example.CapstonProject0.Repository;

import com.example.CapstonProject0.Entity.InquiryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<InquiryEntity, Long> {

    List<InquiryEntity> findByUserId(Long userId);

    List<InquiryEntity> findByStatus(String status);
}
