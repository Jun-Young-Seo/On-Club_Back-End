package com.springboot.club_house_api_server.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class S3AddClubImageDto {
    private MultipartFile logoImageFile;
    private MultipartFile backgroundImageFile;
}
