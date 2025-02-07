package com.springboot.club_house_api_server.excel.dto;

import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ExcelDto {
    private long accountId;
    private long clubId;
    private String excelFilePassword;
    private MultipartFile excelFile;
}
