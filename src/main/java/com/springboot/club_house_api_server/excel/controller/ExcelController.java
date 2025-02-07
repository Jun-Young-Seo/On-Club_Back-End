package com.springboot.club_house_api_server.excel.controller;

import com.springboot.club_house_api_server.excel.dto.ExcelDto;
import com.springboot.club_house_api_server.excel.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/excel")
public class ExcelController {
    private final BudgetService budgetService;
    @PostMapping("/budget")
    public ResponseEntity<?> readExcel(@ModelAttribute ExcelDto excelDto) {
        return budgetService.readBudgetExcel(excelDto);
    }
}
