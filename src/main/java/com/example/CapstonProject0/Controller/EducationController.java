package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.DTO.EducationForm;
import com.example.CapstonProject0.Entity.EducationEntity;
import com.example.CapstonProject0.Repository.EducationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/education")
public class EducationController {

    private final EducationRepository educationRepository;

    public EducationController(EducationRepository educationRepository) {
        this.educationRepository = educationRepository;
    }

    /** 교육 목록 페이지 */
    @GetMapping("/guide")
    public String showEducationList(Model model) {
        List<EducationEntity> educationList = educationRepository.findAll();

        // Entity -> DTO 변환
        List<EducationForm> dtoList = new ArrayList<>();
        for (EducationEntity entity : educationList) {
            EducationForm dto = new EducationForm();
            dto.setId(entity.getId());
            dto.setTitle(entity.getTitle());
            dto.setSummary(entity.getSummary());
            dto.setLink(entity.getLink());
            dto.setRecommendation(entity.getRecommendation());
            dto.setHighlight(entity.getHighlight());

            // 제목에 따라 iconType 결정
            if (entity.getTitle() != null) {
                String title = entity.getTitle();

                if (title.contains("예산")) {
                    dto.setIconType("budget");          // 💰
                } else if (title.contains("저축")) {
                    dto.setIconType("saving");          // 🏦
                } else if (title.contains("이자")) {
                    dto.setIconType("interest");        // 💹
                } else if (title.contains("신용")) {
                    dto.setIconType("credit");          // 💳
                } else if (title.contains("투자")) {
                    dto.setIconType("invest");          // 📊
                } else if (title.contains("대출")) {
                    dto.setIconType("loan");            // 🏠
                } else if (title.contains("보험")) {
                    dto.setIconType("insurance");       // 🛡️
                } else if (title.contains("사기") || title.contains("보이스피싱") || title.contains("피싱")) {
                    dto.setIconType("fraud");           // 🚨
                } else {
                    dto.setIconType("education");       // 기본값 📘
                }
            } else {
                dto.setIconType("education");           // null 예외 처리
            }


            dtoList.add(dto);
        }

        model.addAttribute("educationList", dtoList);
        return "EI/Education-list";
    }

    /** 교육 상세 페이지 */
    @GetMapping("/{id}")
    public String showDetail(@PathVariable("id") Long id, Model model) {
        EducationEntity edu = educationRepository.findById(id).orElseThrow();

        // 상세 페이지는 Entity 그대로 넘겨도 됨
        model.addAttribute("education", edu);
        return "EI/Education-detail";
    }
}
