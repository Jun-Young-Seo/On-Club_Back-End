package com.springboot.club_house_api_server.openai.summary.controller;

import com.springboot.club_house_api_server.openai.summary.entity.AudioSummaryRecord;
import com.springboot.club_house_api_server.openai.summary.repository.AudioSummaryRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AudioFileController {

    @Autowired
    private AudioSummaryRecordRepository recordRepository;

    // DB에 저장된 녹음파일(mp3)을 조회하는 API
    @GetMapping("/audio/{id}")
    public ResponseEntity<?> getAudioFile(@PathVariable Long id) {
        Optional<AudioSummaryRecord> optionalRecord = recordRepository.findById(id);
        if (!optionalRecord.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Record not found");
        }
        AudioSummaryRecord record = optionalRecord.get();

        // 파일 데이터가 있는지 확인
        byte[] fileData = record.getFileData();
        if (fileData == null || fileData.length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("No file data found");
        }

        // ByteArrayResource로 파일 데이터 감싸기
        ByteArrayResource resource = new ByteArrayResource(fileData);

        // mp3 파일의 경우 MIME 타입은 "audio/mpeg"
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(record.getOriginalFilename())
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}