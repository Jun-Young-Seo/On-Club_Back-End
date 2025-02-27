package com.springboot.club_house_api_server.openai.summary.controller;

import com.springboot.club_house_api_server.openai.summary.entity.AudioSummaryRecord;
import com.springboot.club_house_api_server.openai.summary.repository.AudioSummaryRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class AudioUploadController {

    @Autowired
    private AudioSummaryRecordRepository recordRepository;

    // mp3 파일을 DB에 저장하는 API
    @PostMapping("/summary/upload")
    public ResponseEntity<?> uploadAudio(@RequestParam("file") MultipartFile file) {
        try {
            // 파일 이름 및 확장자 검증
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.trim().isEmpty()) {
                return new ResponseEntity<>("파일 이름이 없습니다.", HttpStatus.BAD_REQUEST);
            }

            // mp3만 허용 (원한다면 다른 확장자도 추가 가능)
            String lowerFilename = originalFilename.toLowerCase();
            if (!lowerFilename.endsWith(".mp3")) {
                return new ResponseEntity<>("mp3 파일만 업로드 가능합니다.", HttpStatus.BAD_REQUEST);
            }

            // DB 저장할 엔티티 생성
            AudioSummaryRecord record = new AudioSummaryRecord();
            record.setOriginalFilename(originalFilename);
            record.setFileData(file.getBytes());
            // 필요하다면 record.setCreatedAt(new Date()); (엔티티에 기본값이 있으니 생략 가능)

            // 저장
            recordRepository.save(record);

            return ResponseEntity.ok("파일 업로드 성공! 레코드 ID: " + record.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("파일 업로드 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
