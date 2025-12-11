package com.example.CapstonProject0.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class EducationForm {

    private Long id;
    private String title;
    private String summary;
    private String link;
    private String iconType;
    private String recommendation;
    private String highlight;
}
