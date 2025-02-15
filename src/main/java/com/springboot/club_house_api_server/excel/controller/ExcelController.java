package com.springboot.club_house_api_server.excel.controller;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
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
@RequestMapping("/api/excel")
public class ExcelController {

    @PostMapping("/read")
    public ResponseEntity<?> readEncryptedExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password) {

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.endsWith(".xlsx")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Excel (.xlsx) 파일만 업로드할 수 있습니다.");
        }

        try (InputStream fis = file.getInputStream();
             POIFSFileSystem fs = new POIFSFileSystem(fis)) {

            EncryptionInfo encryptionInfo = new EncryptionInfo(fs);
            Decryptor decryptor = Decryptor.getInstance(encryptionInfo);

            if (!decryptor.verifyPassword(password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 올바르지 않습니다.");
            }

            try (InputStream decryptedStream = decryptor.getDataStream(fs);
                 Workbook workbook = WorkbookFactory.create(decryptedStream)) {

                Sheet sheet = workbook.getSheetAt(0);
                List<Map<String, String>> sheetData = new ArrayList<>();

                for (Row row : sheet) {
                    Map<String, String> rowData = new HashMap<>();
                    for (Cell cell : row) {
                        rowData.put("Column" + cell.getColumnIndex(), cell.toString());
                    }
                    sheetData.add(rowData);
                }

                return ResponseEntity.ok(sheetData);
            }

        } catch (EncryptedDocumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("암호화된 파일을 처리하는 중 문제가 발생했습니다.");
        } catch (GeneralSecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 올바르지 않습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일을 읽는 중 오류가 발생했습니다.");
        }
    }
}
