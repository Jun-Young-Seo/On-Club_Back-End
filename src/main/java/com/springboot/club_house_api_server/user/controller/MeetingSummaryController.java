package com.springboot.club_house_api_server.user.controller;

import com.springboot.club_house_api_server.user.service.MeetingSummaryService;
import com.springboot.club_house_api_server.user.service.OpenAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/meeting")
public class MeetingSummaryController {

    private final OpenAiService openAiService;
    private final MeetingSummaryService meetingSummaryService;

    public MeetingSummaryController(OpenAiService openAiService, MeetingSummaryService meetingSummaryService) {
        this.openAiService = openAiService;
        this.meetingSummaryService = meetingSummaryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadMeetingRecording(@RequestParam("file") MultipartFile file) {
        try {
            // 오디오 파일을 텍스트로 변환
            String transcribedText = openAiService.transcribeAudio(file);

            // 변환된 텍스트를 요약
            String summary = meetingSummaryService.summarizeText(transcribedText);

            return ResponseEntity.ok(summary);  // 요약된 텍스트 반환
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing the file: " + e.getMessage());
        }
    }
}
