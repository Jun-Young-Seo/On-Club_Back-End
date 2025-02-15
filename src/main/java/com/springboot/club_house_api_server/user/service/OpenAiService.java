package com.springboot.club_house_api_server.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Service
public class OpenAiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.key}")  // 환경 변수에서 API 키 불러오기
    private String OPENAI_API_KEY;

    public String transcribeAudio(MultipartFile file) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(OPENAI_API_KEY);  // API 키를 헤더에 추가
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(convertMultipartFileToFile(file)));
        body.add("model", "whisper-1");  // Whisper 모델을 사용하여 텍스트로 변환

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // `exchange` 메서드를 사용하여 제네릭 타입을 처리
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.openai.com/v1/audio/transcriptions",  // Whisper API 엔드포인트
                HttpMethod.POST,  // POST 요청
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}  // 정확한 타입 지정
        );

        // 응답에서 "text" 필드 값 추출
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("text")) {
            return (String) responseBody.get("text");  // 변환된 텍스트 반환
        } else {
            throw new RuntimeException("Text field not found in the response.");
        }
    }

    // MultipartFile을 File 객체로 변환
    private File convertMultipartFileToFile(MultipartFile file) {
        try {
            Path tempFile = Files.createTempFile("audio", file.getOriginalFilename());
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile.toFile();
        } catch (IOException e) {
            throw new RuntimeException("파일 변환 실패", e);
        }
    }
}
