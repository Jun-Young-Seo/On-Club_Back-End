package com.springboot.club_house_api_server.excel.service;

import com.springboot.club_house_api_server.budget.entity.TransactionEntity;
import com.springboot.club_house_api_server.budget.repository.TransactionRepository;
import com.springboot.club_house_api_server.club.account.entity.ClubAccountEntity;
import com.springboot.club_house_api_server.club.account.repository.ClubAccountRepository;
import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.club_data.entity.ClubDataEntity;
import com.springboot.club_house_api_server.excel.dto.ExcelDto;
import com.springboot.club_house_api_server.s3.dto.S3UploadDto;
import com.springboot.club_house_api_server.s3.service.S3Service;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import com.springboot.club_house_api_server.user.repository.UserRepository;
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
    private final ClubAccountRepository clubAccountRepository; //계좌 레포
    private final ClubRepository clubRepository; //클럽 레포
    private final TransactionRepository transactionRepository; //거래내역 레포
    private final CategorizationService categorizationService; //GPT 거래내역 분류 서비스
    private final S3Service s3Service;
    private final UserRepository userRepository;

    public ResponseEntity<?> readBudgetExcel(ExcelDto requestDto) {
        int firstRowNum = 12; //카카오뱅크 내보내기 엑셀 파일은 12행부터 데이터가 시작됨
        List<TransactionEntity> transactionEntityList = new ArrayList<>(); //save All 호출용 리스트

        Optional<ClubAccountEntity> clubAccountOpt = clubAccountRepository.findById(requestDto.getClubId());
        Optional<ClubEntity> clubOpt = clubRepository.findById(requestDto.getClubId());
        Optional<UserEntity> userOpt = userRepository.findById(requestDto.getUserId());

        if(clubAccountOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Id에 해당하는 계좌가 없습니다.");
        }
        if(clubOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club Id에 해당하는 클럽이 없습니다.");
        }
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("userId에 해당하는 유저가 없습니다.");
        }
        ClubAccountEntity account = clubAccountOpt.get();
        ClubEntity club = clubOpt.get();
        UserEntity user = userOpt.get();
        long userId = user.getUserId();

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

        //S3업로드를 위한 예외처리 우선
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
                    String transactionDate = dataFormatter.formatCellValue(row.getCell(1));//2024.10.16 18:04:40
                    LocalDateTime parsedDate = LocalDateTime.parse(transactionDate, dateTimeFormatter);
                    //DB에 이미 저장되어 있는 거래내역인지 먼저 체크
                    boolean alreadySaved = transactionRepository.isAlreadySavedTransaction(account,club,parsedDate);
                    //있다면 루프 건너뛰기
                    if(alreadySaved){
                        continue;
                    }
                    for(Cell cell : row) {
                        String transactionType = dataFormatter.formatCellValue(row.getCell(2));//입금/출금
                        int transactionAmount = parseInt(dataFormatter.formatCellValue(row.getCell(3)));//거래 금액 량
                        int transactionBalance = parseInt(dataFormatter.formatCellValue(row.getCell(4)));//거래 후 잔액
                        String transactionCategory = dataFormatter.formatCellValue(row.getCell(5));//일반입금, 일반이체, 이자 등으로 구분되는 필드
                        String transactionDescription    = dataFormatter.formatCellValue(row.getCell(6));//받는 분 통장에 표시할 내용이 저장되는 필드
                        String transactionMemo = dataFormatter.formatCellValue(row.getCell(7));//카카오뱅크 거래내역 메모 기능을 썼다면 저장되어있음
//                        parsedDate = LocalDateTime.parse(transactionDate, dateTimeFormatter);
                        //없는 경우에만 저장
                        TransactionEntity oneRow = new TransactionEntity(account, club, parsedDate,transactionType,transactionAmount,
                                transactionBalance,transactionCategory,transactionDescription,transactionMemo,null);

                        transactionEntityList.add(oneRow);
                    }
                }
                transactionEntityList = categorizationService.categorizeTransactions(transactionEntityList);
                transactionRepository.saveAll(transactionEntityList);

                //S3 업로드 로직 시작
                S3UploadDto s3UploadDto= new S3UploadDto(club.getClubId(),userId,"budget",file);
                String filePublicUrl = s3Service.uploadFile(s3UploadDto);
                dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                String now = LocalDateTime.now().format(dateTimeFormatter);

                String fileName="budget_"+club.getClubName()+"_"+now+"_"+userId+"_"+file.getOriginalFilename();

                s3Service.saveClubData(club.getClubId(),filePublicUrl,fileName,LocalDateTime.now(),userId,"budget");
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
