package com.springboot.club_house_api_server.summary.controller;

import com.springboot.club_house_api_server.summary.dto.ChatGPTRequest;
import com.springboot.club_house_api_server.summary.dto.ChatGPTResponse;
import com.springboot.club_house_api_server.summary.entity.AudioSummaryRecord;
import com.springboot.club_house_api_server.summary.repository.AudioSummaryRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SummaryFromDbController {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String chatGptApiUrl;

    @Value("${whisper.api.url}")
    private String whisperApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AudioSummaryRecordRepository recordRepository;

    // DB에 저장된 mp3 파일을 조회하여 요약 처리하는 API
    @PostMapping("/summary/from-db/{id}")
    public ResponseEntity<?> processSummaryFromDb(@PathVariable Long id) {
        try {
            // DB에서 해당 레코드 조회
            Optional<AudioSummaryRecord> optionalRecord = recordRepository.findById(id);
            if (!optionalRecord.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Record not found");
            }
            AudioSummaryRecord record = optionalRecord.get();

            // 파일 데이터 확인
            byte[] fileData = record.getFileData();
            if (fileData == null || fileData.length == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No file data found in record");
            }
            String originalFilename = record.getOriginalFilename();

            // Whisper API에 보낼 multipart/form-data 요청 구성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource fileResource = new ByteArrayResource(fileData) {
                @Override
                public String getFilename() {
                    return originalFilename;
                }
            };

            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType("audio/mpeg"));
            fileHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"file\"; filename=\"" + originalFilename + "\"");

            HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(fileResource, fileHeaders);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileEntity);
            body.add("model", "whisper-1");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Whisper API 호출: 음성 전사 수행
            ResponseEntity<String> whisperResponse = restTemplate.exchange(
                    whisperApiUrl, HttpMethod.POST, requestEntity, String.class);
            String transcription = whisperResponse.getBody();
            System.out.println("Transcription: " + transcription);

            // ChatGPT API 호출: 전사된 텍스트 요약 수행
            ChatGPTRequest chatRequest = new ChatGPTRequest(model, transcription);
            ChatGPTResponse chatResponse = restTemplate.postForObject(chatGptApiUrl, chatRequest, ChatGPTResponse.class);
            String summary = chatResponse.getChoices().get(0).getMessage().getContent();

            // DB에 전사 및 요약 결과 업데이트
            record.setTranscription(transcription);
            record.setSummary(summary);
            recordRepository.save(record);

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing record");
        }
    }
}
