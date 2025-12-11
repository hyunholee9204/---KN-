package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.Entity.ChangeType;
import com.example.CapstonProject0.Entity.UpdateEntity;
import com.example.CapstonProject0.Entity.UpdateStatus;
import com.example.CapstonProject0.Service.UpdateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notice/updates")
public class UpdateController {
    private final UpdateService svc;
    public UpdateController(UpdateService svc){
        this.svc=svc;
    }

    @GetMapping
    public String list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "status", required = false) UpdateStatus status,
            Model model) {

        var result = svc.list(status, page, 10);
        model.addAttribute("page", result);
        model.addAttribute("status", status);
        return "Notice/update"; // 목록 템플릿
    }


    // 상세
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model){
        var note = svc.get(id);
        model.addAttribute("note", note);
        return "Notice/update-list";
    }

    // 작성 폼(관리자)
    @GetMapping("/new")
    public String form(Model model){
        model.addAttribute("note", new UpdateEntity());
        return "Notice/updates-form";
    }

    // 작성 처리(관리자)
    @PostMapping
    public String create(@ModelAttribute UpdateEntity note){
        svc.save(note);
        return "redirect:/notice/updates";
    }

    // 간단: 변경항목 추가(상세에서)
    @PostMapping("/{id}/changes")
    public String addChange(@PathVariable Long id,
                            @RequestParam ChangeType type,
                            @RequestParam String description){
        svc.addChange(id, type, description);
        return "redirect:/notice/updates/"+id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id){
        svc.delete(id);
        return "redirect:/notice/updates";
    }
}
