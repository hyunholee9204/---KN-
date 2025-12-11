package com.example.CapstonProject0.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "education_list")
public class EducationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String summary;

    @Column(length = 2000)
    private String content;

    private String highlight;
    private String link;
    private String recommendation;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getContent() {
        return content;
    }

    public String getLink() {
        return link;
    }
    public String getRecommendation() {
        return recommendation;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }
}
