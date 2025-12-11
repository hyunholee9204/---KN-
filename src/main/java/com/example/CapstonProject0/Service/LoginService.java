package com.example.CapstonProject0.Service;

import com.example.CapstonProject0.DTO.SignupForm;
import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Repository.LoginRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final LoginRepository loginRepository;


    public LoginEntity login(String username, String password) {
        LoginEntity user = loginRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void signup(SignupForm signupForm) {
        if (loginRepository.existsByUsername(signupForm.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        LoginEntity user = new LoginEntity();
        user.setUsername(signupForm.getUsername());
        user.setPassword(signupForm.getPassword()); // 암호화 X
        user.setName(signupForm.getName());

        loginRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        loginRepository.deleteById(userId);
    }


}
