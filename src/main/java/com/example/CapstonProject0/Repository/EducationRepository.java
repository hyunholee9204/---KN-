package com.example.CapstonProject0.Repository;

import com.example.CapstonProject0.Entity.EducationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<EducationEntity, Long> {
}
