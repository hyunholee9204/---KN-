package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Repository.LoginRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/find-id")
public class FindIDController {

    private final LoginRepository loginRepository;

    public FindIDController(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    // 아이디 찾기 페이지 이동
    @GetMapping("")
    public String findIdPage() {
        return "login/FindID";
    }

    // 닉네임으로 아이디 찾기
    @PostMapping("")
    @ResponseBody
    public List<String> findIdByNickname(@RequestParam("name") String name) {
        List<LoginEntity> users = loginRepository.findByName(name);

        return users.stream()
                .map(user -> {
                    String id = user.getUsername();
                    if (id.length() > 4)
                        return id.substring(0, id.length() - 4) + "****";
                    else
                        return "****";
                })
                .collect(Collectors.toList());
    }
}
