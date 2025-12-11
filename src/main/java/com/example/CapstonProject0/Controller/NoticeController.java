package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.Entity.NoticeEntity;
import com.example.CapstonProject0.Repository.NoticeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeRepository noticeRepository;

    public NoticeController(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @GetMapping("")
    public String list(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Page<NoticeEntity> noticeEntityPage = noticeRepository.findAllByOrderByIdDesc(
                PageRequest.of(page,10));
        model.addAttribute("noticeEntityPage", noticeEntityPage);
        return "Notice/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        NoticeEntity notice = noticeRepository.findById(id).orElse(null);
        if (notice == null) {
            return "redirect:/notice";
        }

        notice.setViews(notice.getViews() + 1);
        noticeRepository.save(notice);

        model.addAttribute("notice", notice);
        return "Notice/detail";
    }

}
