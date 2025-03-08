package com.springboot.club_house_api_server.s3.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class S3UploadDto {
    private long clubId;
    private long userId;
    private String scenario;
    private MultipartFile file;
}
