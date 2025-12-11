package com.example.CapstonProject0.DTO;

import org.springframework.web.multipart.MultipartFile;

public class InquiryForm {
    private String category;          // 문의 유형 (예: 자산, 대출 등)
    private String title;             // 문의 제목
    private String content;           // 문의 내용
    private String contact;           // 연락처 (선택 입력)
    private Boolean isPrivate;        // 비공개 여부 체크박스
    private MultipartFile file;

    public String getCategory() {
        return category;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getContact() {
        return contact;
    }
    public Boolean getIsPrivate() {
        return isPrivate;
    }
    public MultipartFile getFile() {
        return file;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
