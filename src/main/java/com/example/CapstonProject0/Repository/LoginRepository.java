package com.example.CapstonProject0.Repository;

import com.example.CapstonProject0.Entity.LoginEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginRepository extends JpaRepository<LoginEntity, Long> {
    LoginEntity findByUsername(String username);
    boolean existsByUsername(String username);
    List<LoginEntity> findByName(String name);
    LoginEntity findByUsernameAndName(String username, String name);
    void deleteById(Long id);
}
