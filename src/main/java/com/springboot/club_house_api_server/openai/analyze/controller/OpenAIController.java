package com.springboot.club_house_api_server.openai.analyze.controller;

import com.springboot.club_house_api_server.openai.analyze.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/openai")
public class OpenAIController {
    private final OpenAIService openAIService;

    @PostMapping("/analyze/budget")
    public ResponseEntity<?> analyzeBudgetExcel(){
       return null;
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody String prompt){
        String response = openAIService.getGptResponse(prompt);
        return ResponseEntity.ok().body(response);
    }

}
