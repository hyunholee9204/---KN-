package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Repository.LoginRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/find-pw")
public class FindPWController {

    private final LoginRepository loginRepository;

    public FindPWController(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    // 비밀번호 찾기 페이지 이동
    @GetMapping("")
    public String findPwPage() {
        return "login/FindPW";
    }

    // 아이디 + 닉네임으로 비밀번호 찾기
    @PostMapping("")
    @ResponseBody
    public String findPassword(@RequestParam("username") String username,
                               @RequestParam("name") String name) {

        LoginEntity user = loginRepository.findByUsernameAndName(username, name);
        if (user == null) {
            return "존재하지 않는 사용자입니다.";
        }

        String pw = user.getPassword();
        if (pw.length() > 4)
            return pw.substring(0, pw.length() - 4) + "****";
        else
            return "****";
    }
}
