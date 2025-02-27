package com.springboot.club_house_api_server.openai.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIConfig {
    @Value("${openai.api.key}")
    private String openAIApiKey;

    @Value("${openai.api.url}")
    private String openAIURL;

    @Value("${openai.model}")
    private String openAIModel;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openAIApiKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
