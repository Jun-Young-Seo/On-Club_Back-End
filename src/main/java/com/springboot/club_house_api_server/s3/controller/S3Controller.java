package com.springboot.club_house_api_server.s3.controller;

import com.springboot.club_house_api_server.s3.dto.S3UploadDto;
import com.springboot.club_house_api_server.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/s3")
public class S3Controller {
    private final S3Service s3Service;

    @PostMapping("/upload-file")
    public String uploadFile(@ModelAttribute S3UploadDto dto) {
        System.out.println(dto.toString());
        try {
            String filePublicUrl = s3Service.uploadFile(dto);
            return "File 업로드 성공 : " + filePublicUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "File upload failed!";
        }
    }
}

