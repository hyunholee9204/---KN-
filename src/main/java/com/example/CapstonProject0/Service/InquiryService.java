package com.example.CapstonProject0.Service;

import com.example.CapstonProject0.DTO.InquiryForm;
import com.example.CapstonProject0.Entity.InquiryEntity;
import com.example.CapstonProject0.Repository.InquiryRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    @Value("${file.upload-dir}") // application.yml에 지정된 경로
    private String uploadDir;

    public InquiryService(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    public void saveInquiry(InquiryForm form, Long userId) throws IOException {
        InquiryEntity inquiry = new InquiryEntity();
        inquiry.setUserId(userId);
        inquiry.setCategory(form.getCategory());
        inquiry.setTitle(form.getTitle());
        inquiry.setContent(form.getContent());
        inquiry.setContact(form.getContact());
        inquiry.setIsPrivate(form.getIsPrivate());

        MultipartFile file = form.getFile();
        if (file != null && !file.isEmpty()) {
            String filename = file.getOriginalFilename();
            String filepath = uploadDir + "/" + filename;
            file.transferTo(new File(filepath));
            inquiry.setFilename(filename);
            inquiry.setFilepath(filepath);
        }

        inquiryRepository.save(inquiry);
    }

    public List<InquiryEntity> findByUserId(Long userId) {
        return inquiryRepository.findByUserId(userId);
    }

}
