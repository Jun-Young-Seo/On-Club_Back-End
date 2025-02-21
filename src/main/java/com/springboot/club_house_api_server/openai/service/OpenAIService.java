package com.springboot.club_house_api_server.openai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAIService {
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String gptModel;

    public OpenAIService(@Value("{openai.api.url}")String apiUrl,
                         @Value("{openai.model}")String gptModel,
                         RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.gptModel = gptModel;
        this.restTemplate = restTemplate;
    }

}
