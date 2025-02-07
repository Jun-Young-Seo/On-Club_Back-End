package com.springboot.club_house_api_server.excel.service;

import com.springboot.club_house_api_server.budget.entity.TransactionEntity;
import com.springboot.club_house_api_server.budget.repository.AccountRepository;
import com.springboot.club_house_api_server.budget.repository.TransactionRepository;
import com.springboot.club_house_api_server.club.account.entity.ClubAccountEntity;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.excel.dto.ExcelDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final AccountRepository accountRepository;
    private final ClubRepository clubRepository;
    private final TransactionRepository transactionRepository;

    public ResponseEntity<?> readBudgetExcel(ExcelDto requestDto) {
        int firstRowNum = 12; //카카오뱅크 내보내기 엑셀 파일은 12행부터 데이터가 시작됨
        List<TransactionEntity> transactionEntityList = new ArrayList<>(); //save All 호출용 리스트
        ClubAccountEntity account = accountRepository.findById(requestDto.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account ID에 해당하는 계좌가 없습니다 Account ID: " + requestDto.getAccountId()));

        ClubEntity club = clubRepository.findById(requestDto.getClubId())
                .orElseThrow(() -> new IllegalArgumentException("Club ID에 해당하는 동호회가 없습니다. Club ID: " + requestDto.getClubId()));

        MultipartFile file = requestDto.getExcelFile();
        String password = requestDto.getExcelFilePassword();
        ResponseEntity<?> response;//오류 응답용
        DataFormatter dataFormatter = new DataFormatter();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
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

                Sheet sheet = workbook.getSheetAt(0);
                for(int i=firstRowNum; i<=sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if(row==null){
                        continue;
                    }
                    for(Cell cell : row) {
                        CellReference cellReference = new CellReference(row.getRowNum(), cell.getColumnIndex());

                        String transactionDate = dataFormatter.formatCellValue(row.getCell(1));//2024.10.16 18:04:40
                        String transactionType = dataFormatter.formatCellValue(row.getCell(2));//입금/출금
                        int transactionAmount = parseInt(dataFormatter.formatCellValue(row.getCell(3)));//거래 금액 량
                        int transactionBalance = parseInt(dataFormatter.formatCellValue(row.getCell(4)));//거래 후 잔액
                        String transactionCategory = dataFormatter.formatCellValue(row.getCell(5));//일반입금, 일반이체, 이자 등으로 구분되는 필드
                        String transactionDescription    = dataFormatter.formatCellValue(row.getCell(6));//받는 분 통장에 표시할 내용이 저장되는 필드
                        String transactionMemo = dataFormatter.formatCellValue(row.getCell(7));//카카오뱅크 거래내역 메모 기능을 썼다면 저장되어있음
                        LocalDateTime parsedDate = LocalDateTime.parse(transactionDate, dateTimeFormatter);

                        TransactionEntity oneRow = new TransactionEntity(account, club, parsedDate,transactionType,transactionAmount,
                                transactionBalance,transactionCategory,transactionDescription,transactionMemo);
                        transactionEntityList.add(oneRow);
                    }
                }
                transactionRepository.saveAll(transactionEntityList);
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

    private int parseInt(String str) {
        return Integer.parseInt(str.replace(",",""));
    }

    private LocalDateTime dateStringToLocalDateTime(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
        return LocalDateTime.parse(dateStr, formatter);
    }
}
