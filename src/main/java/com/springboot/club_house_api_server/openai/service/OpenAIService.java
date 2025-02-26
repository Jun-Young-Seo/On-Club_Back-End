package com.springboot.club_house_api_server.openai.service;

import com.springboot.club_house_api_server.openai.dto.CustomRequestDto;
import com.springboot.club_house_api_server.openai.dto.MessageDto;
import com.springboot.club_house_api_server.openai.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class OpenAIService {
    private final RestTemplate restTemplate;
    private final String openAIURL;
    private final String gptModel;

    public OpenAIService(@Value("${openai.api.url}")String openAIURL,
                         @Value("${openai.model}")String gptModel,
                         RestTemplate restTemplate) {
        this.openAIURL = openAIURL;
        this.gptModel = gptModel;
        this.restTemplate = restTemplate;
    }
    public String getGptResponse(String prompt) {
        CustomRequestDto requestDto = new CustomRequestDto(
                gptModel,
                List.of(new MessageDto("user", prompt))
        );

        ResponseDto response = restTemplate.postForObject(openAIURL, requestDto, ResponseDto.class);

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        }

        return "OpenAI 응답이 없습니다.";
    }

}
