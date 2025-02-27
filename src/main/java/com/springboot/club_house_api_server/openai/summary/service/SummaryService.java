package com.springboot.club_house_api_server.openai.summary.service;

import com.springboot.club_house_api_server.openai.summary.dto.ChatGPTRequest;
import com.springboot.club_house_api_server.openai.summary.dto.ChatGPTResponse;
import com.springboot.club_house_api_server.openai.summary.entity.AudioSummaryRecord;
import com.springboot.club_house_api_server.openai.summary.repository.AudioSummaryRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class SummaryService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.api.url}")
    private String apiURL;

    @Value("${openai.model}")
    private String model;

    // DB 저장을 위한 Repository 주입
    @Autowired
    private AudioSummaryRecordRepository audioSummaryRecordRepository;

    // 텍스트 요약하는 메서드
    public String getSummary(String text) {
        ChatGPTRequest request = new ChatGPTRequest(model, text);
        ChatGPTResponse response = restTemplate.postForObject(apiURL, request, ChatGPTResponse.class);
        return response.getChoices().get(0).getMessage().getContent();
    }

    // DB에 전사 텍스트, 요약 결과, 파일명을 저장하는 메서드 추가
    public void saveSummary(String originalFilename, String transcription, String summary) {
        AudioSummaryRecord record = new AudioSummaryRecord();
        record.setOriginalFilename(originalFilename);
        record.setTranscription(transcription);
        record.setSummary(summary);
        record.setCreatedAt(new Date());
        audioSummaryRecordRepository.save(record);
    }
}
