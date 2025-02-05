package com.springboot.club_house_api_server.excel.service;

import lombok.AllArgsConstructor;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@AllArgsConstructor
@Service
public class BudgetService {
    public ResponseEntity<?> readBudgetExcel(MultipartFile file, String password, int pageNum) {
        int firstRowNum = 12; //카카오뱅크 내보내기 엑셀 파일은 12행부터 데이터가 시작됨
        ResponseEntity<?> response;//오류 응답용
        DataFormatter dataFormatter = new DataFormatter();
        if (file.isEmpty()) {
            response = excelException(HttpStatus.BAD_REQUEST, "파일을 찾을 수 없습니다.");
            return response;
        }

        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.endsWith(".xlsx")) {
            response = excelException(HttpStatus.BAD_REQUEST,".xlsx 확장자 파일만 업로드 할 수 있습니다.");
        }

        try (InputStream fis = file.getInputStream();
             POIFSFileSystem fs = new POIFSFileSystem(fis)) {

            EncryptionInfo encryptionInfo = new EncryptionInfo(fs);
            Decryptor decryptor = Decryptor.getInstance(encryptionInfo);

            if (!decryptor.verifyPassword(password)) {
                response = excelException(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다.");
                return response;
            }

            try (InputStream decryptedStream = decryptor.getDataStream(fs);
                 Workbook workbook = WorkbookFactory.create(decryptedStream)) {

                Sheet sheet = workbook.getSheetAt(pageNum);
                for(int i=firstRowNum; i<=sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    for(Cell cell : row) {
                        CellReference cellReference = new CellReference(row.getRowNum(), cell.getColumnIndex());

                        String dateStr = dataFormatter.formatCellValue(row.getCell(0));
                        String transactionType = dataFormatter.formatCellValue(row.getCell(1));
                        String amount = dataFormatter.formatCellValue(row.getCell(2));
                        String balance = dataFormatter.formatCellValue(row.getCell(3));
                        String method = dataFormatter.formatCellValue(row.getCell(4));
                        String client = dataFormatter.formatCellValue(row.getCell(5));
                        String description = dataFormatter.formatCellValue(row.getCell(6));
                        LocalDateTime date = dateStringToLocalDateTime(dateStr);


                    }
                }

                return ResponseEntity.ok("DB에 저장 완료");
            }

        } catch (EncryptedDocumentException e) {
            return excelException(HttpStatus.UNAUTHORIZED,"파일의 복호화 중 문제 발생"+e.getMessage());
        } catch (GeneralSecurityException e) {
            return excelException(HttpStatus.UNAUTHORIZED,"파일의 복호화 중 문제 발생"+e.getMessage());
        } catch (IOException e) {
            return excelException(HttpStatus.UNAUTHORIZED,"파일을 읽는 도중 문제 발생"+e.getMessage());
        }
    }
    public ResponseEntity<?> excelException(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(message);
    }

    private boolean emptyRow(Row row) {
        for (Cell cell : row) {
            if(cell!=null && cell.getCellType()!=CellType.BLANK){
                return false;
            }
        }
        return true;
    }

    private LocalDateTime dateStringToLocalDateTime(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
        return LocalDateTime.parse(dateStr, formatter);
    }
}
