package com.example.CapstonProject0.Repository;

import com.example.CapstonProject0.Entity.UpdateDetailEntity;
import com.example.CapstonProject0.Entity.UpdateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UpdateDetailRepository extends JpaRepository<UpdateDetailEntity, Long> {
    List<UpdateDetailEntity> findByNoteOrderByIdAsc(UpdateEntity note);
}
