package com.springboot.club_house_api_server.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MeetingSummaryService {

    @Value("${openai.api.key}")  // API 키 설정
    private String OPENAI_API_KEY;

    private final RestTemplate restTemplate = new RestTemplate();

    // 텍스트를 요약하는 메서드
    public String summarizeText(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(OPENAI_API_KEY);  // API 키 설정
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{ \"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"" + text + "\"}]}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",  // GPT 모델을 사용하여 텍스트 요약
                HttpMethod.POST,
                entity,
                String.class
        );

        return response.getBody();  // 요약된 텍스트 반환
    }
}
