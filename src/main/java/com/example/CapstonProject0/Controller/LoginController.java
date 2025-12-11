package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.DTO.LoginForm;
import com.example.CapstonProject0.DTO.SignupForm;
import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "Login/login"; // 로그인 페이지 템플릿
    }

    @PostMapping("/login")
    public String Login(LoginForm loginForm, HttpSession session, Model model) {

        System.out.println("입력된 username: " + loginForm.getUsername());
        System.out.println("입력된 password: " + loginForm.getPassword());

        LoginEntity user = loginService.login(loginForm.getUsername(), loginForm.getPassword());

        if (user != null) {
            session.setAttribute("loginUser", user);
            return "redirect:/main";
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 틀렸습니다.");
            return "Login/login";
        }
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "Login/Signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignupForm signupForm, Model model) {
        try {
            loginService.signup(signupForm);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/signup";

        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "로그아웃 되었습니다.");
        return "redirect:/main";
    }

    @PostMapping("/user/delete")
    public String deleteUser(HttpSession session, RedirectAttributes redirectAttributes) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user != null) {
            loginService.deleteUser(user.getId());
            session.invalidate();  // 로그아웃 처리
        }
        return "redirect:/main"; // 탈퇴 후 메인으로 이동
    }

}
