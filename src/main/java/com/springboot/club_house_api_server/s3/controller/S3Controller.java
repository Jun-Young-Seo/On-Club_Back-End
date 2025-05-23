package com.springboot.club_house_api_server.s3.controller;

import com.springboot.club_house_api_server.s3.dto.S3AddClubImageDto;
import com.springboot.club_house_api_server.s3.dto.S3UploadDto;
import com.springboot.club_house_api_server.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/s3")
public class S3Controller {
    private final S3Service s3Service;

    @PostMapping("/add/club/images")
    public ResponseEntity<?> uploadImageForAddClub(@ModelAttribute S3AddClubImageDto dto) throws IOException {
        return s3Service.uploadImageForAddClub(dto);
    }
    @PostMapping("/upload-file")
    public String uploadFile(@ModelAttribute S3UploadDto dto) {
        System.out.println(dto.toString());
        try {
            return s3Service.uploadFile(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return "File upload failed!";
        }
    }
}

