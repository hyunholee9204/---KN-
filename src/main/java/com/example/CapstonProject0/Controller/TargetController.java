package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.Entity.LoginEntity;
import com.example.CapstonProject0.Entity.TargetEntity;
import com.example.CapstonProject0.Entity.TargetHistoryEntity;
import com.example.CapstonProject0.Service.TargetService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/target")
public class TargetController {

    private final TargetService targetService;

    // ✅ 목표 메인 페이지
    @GetMapping("")
    public String targetMain(HttpSession session, Model model) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        // DB에 저장된 목표 목록
        List<TargetEntity> targets = targetService.getTargetsByUser(user);

        // 진행률 자동 갱신
        for (TargetEntity target : targets) {
            targetService.refreshTargetProgress(target.getId());
        }

        // 최신 데이터 다시 로드
        targets = targetService.getTargetsByUser(user);
        model.addAttribute("targets", targets);

        return "Target/Target_Tracking";
    }

    // ✅ 목표 등록 폼 페이지
    @GetMapping("/register")
    public String targetRegisterForm(Model model) {
        model.addAttribute("target", new TargetEntity());
        return "Target/Target-register";
    }

    // ✅ 목표 등록 처리
    @PostMapping("/register")
    public String targetRegisterSubmit(@ModelAttribute TargetEntity target, HttpSession session) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        target.setUser(user);
        target.setTotalAmount(0L);
        targetService.saveTarget(target);
        return "redirect:/target";
    }

    // ✅ 목표 수정 폼 페이지
    @GetMapping("/edit/{id}")
    public String editTargetForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        TargetEntity target = targetService.getTargetById(id);
        if (target == null || !target.getUser().getId().equals(user.getId())) {
            return "redirect:/target";
        }

        model.addAttribute("target", target);
        return "Target/Target-edit";
    }

    // ✅ 목표 수정 처리
    @PostMapping("/edit/{id}")
    public String updateTarget(@PathVariable("id") Long id,
                               @ModelAttribute TargetEntity target,
                               HttpSession session) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        targetService.updateTarget(id, target, user);
        return "redirect:/target";
    }

    // ✅ 목표 삭제
    @PostMapping("/delete/{id}")
    public String targetDelete(@PathVariable("id") Long id, HttpSession session) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        targetService.deleteTarget(id, user);
        return "redirect:/target";
    }

    // ✅ 목표 리포트 페이지
    @GetMapping("/report")
    public String targetReport() {
        return "Target/Target-report";
    }

    // ✅ 캘린더 이벤트 데이터 반환
    @GetMapping("/calendar-events")
    @ResponseBody
    public List<Map<String, Object>> getTargetEvents(HttpSession session) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return new ArrayList<>();

        List<TargetEntity> targets = targetService.getTargetsByUser(user);
        List<Map<String, Object>> events = new ArrayList<>();

        for (TargetEntity target : targets) {
            String color = getColorByTargetId(target.getId());

            Map<String, Object> startEvent = new HashMap<>();
            startEvent.put("title", target.getTitle());
            startEvent.put("start", target.getStartDate().toString());
            startEvent.put("end", target.getStartDate().plusDays(7).toString());
            startEvent.put("color", color);
            events.add(startEvent);

            Map<String, Object> endEvent = new HashMap<>();
            endEvent.put("title", target.getTitle());
            endEvent.put("start", target.getEndDate().minusDays(7).toString());
            endEvent.put("end", target.getEndDate().plusDays(1).toString());
            endEvent.put("color", color);
            events.add(endEvent);
        }
        return events;
    }

    // ✅ 목표 ID 기반 색상 고정
    private String getColorByTargetId(Long id) {
        String[] colors = {"#4CAF50", "#2196F3", "#FF9800", "#9C27B0"};
        return colors[(int)(id % colors.length)];
    }

    @GetMapping("/progress")
    public String showTargetProgress(HttpSession session, Model model) {
        LoginEntity user = (LoginEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        List<TargetEntity> targets = targetService.getTargetsByUser(user);
        List<Map<String, Object>> targetDataList = new ArrayList<>();

        for (TargetEntity t : targets) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", t.getId());
            data.put("title", t.getTitle());
            data.put("goalAmount", t.getGoalAmount());
            data.put("totalAmount", t.getTotalAmount());
            data.put("progressRate", targetService.calculateProgress(t));
            data.put("daysLeft", targetService.getDaysLeft(t));

            // ✅ 저축/출금 내역
            List<TargetHistoryEntity> history = targetService.getTargetHistory(t.getId());
            data.put("history", history);

            // ✅ 15일 단위 그래프 데이터
            Map<String, Long> biweeklyData = targetService.getBiweeklyChangeData(t.getId());
            data.put("biweeklyData", biweeklyData);  // 🔥 이걸 꼭 추가해야 함
            data.put("labels", new ArrayList<>(biweeklyData.keySet()));
            data.put("values", new ArrayList<>(biweeklyData.values()));

            targetDataList.add(data);
        }

        model.addAttribute("targets", targetDataList);
        return "target/target-progress";
    }


}
